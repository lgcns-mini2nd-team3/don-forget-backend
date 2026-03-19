package com.example.notification_service.ctrl;

import com.example.notification_service.domain.Notification;
import com.example.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 내 알림 목록 조회 (GET)
    @GetMapping
    public List<Notification> getNotifications(@RequestParam Long userId) {
        return notificationService.getNotifications(userId);
    }

    // 알림 생성 (POST)
    @PostMapping
    public void createNotification(@RequestBody NotificationRequest request) {
        notificationService.createNotification(
            request.getUserId(), 
            request.getPaymentId(), 
            request.getType(), 
            request.getMessage()
        );
    }

    // 3. 알림 읽음 처리 (PATCH) - 사용자가 클릭했을 때
    @PatchMapping("/{id}/read")
    public void readNotification(@PathVariable Long id) {
        notificationService.readNotification(id);
    }

    // 4. 안 읽은 알림 개수 (GET)
    @GetMapping("/unread-count")
    public long getUnreadCount(@RequestParam Long userId) {
        return notificationService.getUnreadCount(userId);
    }

    // 5. 알림 삭제 (DELETE)
    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }

    // 알림 생성을 위한 데이터 가방 (DTO)
    @lombok.Getter
    @lombok.NoArgsConstructor
    public static class NotificationRequest {
        private Long userId;
        private Long paymentId;
        private String type;
        private String message;
    }
}