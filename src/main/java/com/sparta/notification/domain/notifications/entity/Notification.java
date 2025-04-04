package com.sparta.notification.domain.notifications.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private String message;

    @Column(nullable = false)
    private boolean readStatus = false;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public void changeStatusRead() {
        readStatus = true;
    }

    public Notification(Long userId, EventType eventType, String message) {
        this.userId = userId;
        this.eventType = eventType;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }
}
