package com.sparta.notification.domain.notifications.repository;

import com.sparta.notification.domain.notifications.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReadStatusTrueAndCreatedAtBefore(LocalDateTime date);

    List<Notification> findAllByUserIdOrderByCreatedAt(Long userId);
}
