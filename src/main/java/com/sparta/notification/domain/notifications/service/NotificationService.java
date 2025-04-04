package com.sparta.notification.domain.notifications.service;

import com.sparta.notification.domain.notifications.dto.NotificationMessage;
import com.sparta.notification.domain.notifications.entity.Notification;
import com.sparta.notification.domain.notifications.event.SseEmitterHandler;
import com.sparta.notification.domain.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseEmitterHandler sseEmitterHandler;

    public SseEmitter subscribe(Long userId) {
        String topic = "notifications-" + userId;
        log.info("✅ User {} subscribed to topic: {}", userId, topic);
        return sseEmitterHandler.addEmitter(topic);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.changeStatusRead();
        notificationRepository.save(notification);
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    // 14 일 이상 오래된 알림 삭제
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deleteOldNotifications() {
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(14);
        List<Notification> oldNotifications = notificationRepository.findByReadStatusTrueAndCreatedAtBefore(thresholdDate);
        notificationRepository.deleteAll(oldNotifications);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findAllByUserIdOrderByCreatedAt(userId);
    }
}
