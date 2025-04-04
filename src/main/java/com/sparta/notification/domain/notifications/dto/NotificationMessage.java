package com.sparta.notification.domain.notifications.dto;

import com.sparta.notification.domain.notifications.entity.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private Long userId;
    private EventType eventType;
    private String message;
}