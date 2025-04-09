package com.sparta.notification.domain.notifications.controller;

import com.sparta.notification.domain.notifications.client.UserClient;
import com.sparta.notification.domain.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserClient userClient;

    @GetMapping("/v1/notifications/subscribe")
    public SseEmitter subscribe(
            @RequestHeader("X-User-Id") Long userId
    ) {
        log.info("🔔 구독 요청 - userId: {}", userId);

        SseEmitter emitter = notificationService.subscribe(userId);

        // 미확인 알림 전송
        notificationService.sendUnreadNotifications(userId);

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
