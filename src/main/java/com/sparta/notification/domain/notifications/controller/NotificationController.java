package com.sparta.notification.domain.notifications.controller;

import com.sparta.notification.domain.notifications.entity.Notification;
import com.sparta.notification.domain.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/v1/notifications/{userId}/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long userId) {
        log.info("🔔 Subscription request received for userId: {}", userId);
        SseEmitter emitter = notificationService.subscribe(userId);

        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        notifications.forEach(notification -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data("EventType: " + notification.getEventType() + ", Message: " + notification.getMessage()));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    @PatchMapping("/v1/notifications/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/v1/notifications/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
