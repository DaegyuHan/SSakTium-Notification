package com.sparta.notification.domain.notifications.event;

import com.sparta.notification.domain.notifications.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaListenerHandler {

    private final SseEmitterHandler sseEmitterHandler;

    @KafkaListener(topics = "notifications", groupId = "notification-group")
    public void consume(NotificationMessage message) {

        String topic = "notifications-" + message.getUserId();
        String data = "EventType: " + message.getEventType() + ", Message: " + message.getMessage();

        sseEmitterHandler.broadcast(topic, data);
    }
}