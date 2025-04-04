package com.sparta.notification.domain.notifications.event;

import com.sparta.notification.domain.notifications.dto.NotificationMessage;
import com.sparta.notification.domain.notifications.entity.Notification;
import com.sparta.notification.domain.notifications.repository.NotificationRepository;
import com.sparta.notification.domain.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        Notification notification = new Notification(message.getUserId(), message.getEventType(), message.getMessage());
        log.info("notification DB 저장");
        notificationRepository.save(notification);
        sseEmitterHandler.broadcast(topic, data);
    }
}
