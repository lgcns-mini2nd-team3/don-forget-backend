package com.example.notification_service.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private Long userId;    // 수신자 ID
    
    @Column(name = "payment_id") 
    private Long invoiceId;

    private String type;    // BEFORE_DUE, ON_DUE, OVERDUE
    private String message; // 알림 내용

    @Builder.Default
    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean isRead = false; // 읽음 여부 (기본값 false)

    @Builder.Default
    private LocalDateTime sentAt = LocalDateTime.now(); // 발송 시각

    // 알림 읽음 처리 기능
    public void markAsRead() {
        this.isRead = true;
    }
}