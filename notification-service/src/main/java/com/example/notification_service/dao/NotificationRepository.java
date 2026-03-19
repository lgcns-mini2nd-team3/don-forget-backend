package com.example.notification_service.dao;

import com.example.notification_service.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 특정 유저의 알림을 최신순으로 가져오기
    List<Notification> findAllByUserIdOrderBySentAtDesc(Long userId);
    
    // 안 읽은 알림 개수 세기
    long countByUserIdAndIsReadFalse(Long userId);
}