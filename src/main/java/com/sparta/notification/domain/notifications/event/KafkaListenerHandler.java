package com.sparta.notification.domain.notifications.event;

import com.sparta.notification.domain.notifications.client.UserClient;
import com.sparta.notification.domain.notifications.dto.FollowerResponseDto;
import com.sparta.notification.domain.notifications.dto.NotificationMessage;
import com.sparta.notification.domain.notifications.dto.UserInfoResponseDto;
import com.sparta.notification.domain.notifications.entity.EventType;
import com.sparta.notification.domain.notifications.entity.Notification;
import com.sparta.notification.domain.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaListenerHandler {

    private final SseEmitterHandler sseEmitterHandler;
    private final NotificationRepository notificationRepository;
    private final UserClient userClient;

    @KafkaListener(topics = "notifications", groupId = "notification-group")
    public void consume(NotificationMessage message) {
        EventType eventType = message.getEventType();

        if (eventType == EventType.POST_CREATED) {
            handlePostCreated(message);
        } else {
            handleSingleTarget(message);
        }
    }

    // 팔로우 요청 알림
    private void handleSingleTarget(NotificationMessage message) {
        try {
            Long receiverId = message.getUserId();
            String content = message.getMessage();

            Notification notification = new Notification(receiverId, message.getEventType(), content);
            notificationRepository.save(notification);

            String topic = "notifications-" + receiverId;
            if (sseEmitterHandler.hasEmitter(topic)) {
                sseEmitterHandler.broadcast(topic, content);
            }

            log.info("🟢 단일 알림 전송 완료 (유저 {})", receiverId);
        } catch (Exception e) {
            log.error("❌ 단일 알림 처리 중 예외 발생: {}", e.getMessage(), e);
        }
    }

    // 게시글 등록 알림
    private void handlePostCreated(NotificationMessage message) {
        Long authorId = message.getUserId();
        String content = message.getMessage();

        try {
            List<FollowerResponseDto> followers = userClient.getFollowerIds(authorId);

            for (FollowerResponseDto follower : followers) {
                Notification notification = new Notification(follower.getUserId(), EventType.POST_CREATED, content);
                notificationRepository.save(notification);

                String topic = "notifications-" + follower.getUserId();
                if (sseEmitterHandler.hasEmitter(topic)) {
                    sseEmitterHandler.broadcast(topic, content);
                }
            }

            log.info("🟡 게시글 알림: {}명의 팔로워에게 전송 완료", followers.size());

        } catch (Exception e) {
            log.error("❌ 게시글 알림 처리 중 예외 발생: {}", e.getMessage(), e);
        }
    }
}
