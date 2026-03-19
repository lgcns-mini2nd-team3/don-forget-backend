package com.example.notification_service.service;

import com.example.notification_service.dao.NotificationRepository;
import com.example.notification_service.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // 알림 목록 조회
    public List<Notification> getNotifications(Long userId) {
        return notificationRepository.findAllByUserIdOrderBySentAtDesc(userId);
    }

    // 알림 생성
    @Transactional
    public void createNotification(Long userId, Long paymentId, String type, String message) {
        Notification notification = Notification.builder()
                .userId(userId)
                .paymentId(paymentId)
                .type(type)
                .message(message)
                .build();

        notificationRepository.save(notification);
    }

    // 알림 읽음 처리
    @Transactional
    public void readNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 알림이 존재하지 않습니다."));
        notification.markAsRead();
    }

    // 알림 삭제
    @Transactional
    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제하려는 알림이 존재하지 않습니다. id=" + id));
        
        notificationRepository.delete(notification);
    }

    // 안 읽은 알림 개수
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
}