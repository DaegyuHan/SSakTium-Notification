package com.sparta.notification.domain.notifications.controller;

import com.sparta.notification.domain.common.AuthTokenHolder;
import com.sparta.notification.domain.notifications.client.UserClient;
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
    private final UserClient userClient;

    @GetMapping("/v1/notifications/subscribe")
    public SseEmitter subscribe(@RequestHeader("Authorization") String authHeader) {
        log.info("🔔 구독 완료");

        // 토큰 저장
        AuthTokenHolder.setToken(authHeader);

        // 이후 서비스 로직
        Long userId = userClient.getMyInfo().getUserId();
        SseEmitter emitter = notificationService.subscribe(userId);

        // 미확인 알림 전송
        notificationService.sendUnreadNotifications(userId);

        // ThreadLocal 정리
        AuthTokenHolder.clear();

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
