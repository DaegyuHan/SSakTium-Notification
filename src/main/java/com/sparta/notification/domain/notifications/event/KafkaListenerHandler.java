package com.sparta.notification.domain.notifications.event;

import com.sparta.notification.domain.notifications.dto.NotificationMessage;
import com.sparta.notification.domain.notifications.entity.Notification;
import com.sparta.notification.domain.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaListenerHandler {

    private final SseEmitterHandler sseEmitterHandler;
    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = "notifications", groupId = "notification-group")
    public void consume(NotificationMessage message) {
        log.info("📥 Received Kafka message: {}", message);

        String topic = "notifications-" + message.getUserId();
        String data = "EventType: " + message.getEventType() + ", Message: " + message.getMessage();

        // 알림 저장
        Notification notification = new Notification(message.getUserId(), message.getEventType(), message.getMessage());
        notificationRepository.save(notification);
        log.info("📦 DB 저장 완료");

        // emitter 가 존재하면 알림 전송
        if (sseEmitterHandler.hasEmitter(topic)) {
            sseEmitterHandler.broadcast(topic, data);
            log.info("📤 유저 {}에게 실시간 알림 전송", message.getUserId());
        } else {
            log.info("📭 유저 {}는 접속 상태 아님 (알림 전송 보류)", message.getUserId());
        }
    }
}
