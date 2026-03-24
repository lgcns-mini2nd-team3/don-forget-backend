package com.example.notification_service.ctrl;

import com.example.notification_service.domain.Notification;
import com.example.notification_service.dto.request.CreateNotificationRequest;
import com.example.notification_service.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "알림 관리 API (알림 조회, 읽음 처리 등)")
public class NotificationController {

    private final NotificationService notificationService;

    // 내 알림 목록 조회 (GET)
    @Operation(summary = "내 알림 목록 조회", description = "특정 사용자의 전체 알림 목록을 가져옵니다.")
    @GetMapping
    public List<Notification> getNotifications(@RequestParam Long userId) {
        return notificationService.getNotifications(userId);
    }

    // 알림 생성 (POST)
    @Operation(summary = "알림 생성", description = "새로운 알림을 생성합니다.")
    @PostMapping
    public void createNotification(@Valid @RequestBody CreateNotificationRequest request) {
        notificationService.createNotification(
            request.getUserId(), 
            request.getPaymentId(), 
            request.getType(), 
            request.getMessage()
        );
    }

    // 3. 알림 읽음 처리 (PATCH) - 사용자가 클릭했을 때
    @Operation(summary = "알림 읽음 처리", description = "알림 ID를 받아 읽음 상태로 변경합니다.")
    @PatchMapping("/{id}/read")
    public void readNotification(@PathVariable Long id) {
        notificationService.readNotification(id);
    }

    // 4. 안 읽은 알림 개수 (GET)
    @Operation(summary = "안 읽은 알림 개수 조회", description = "특정 유저의 안 읽은 알림 개수를 조회합니다.")
    @GetMapping("/unread-count")
    public long getUnreadCount(@RequestParam Long userId) {
        return notificationService.getUnreadCount(userId);
    }

    // 5. 알림 삭제 (DELETE)
    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다.")
    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }
}