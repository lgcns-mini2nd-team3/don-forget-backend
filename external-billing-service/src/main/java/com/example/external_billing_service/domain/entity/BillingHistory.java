package com.example.external_billing_service.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "billing_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BillingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "due_day")
    private Integer dueDay;

    @Column(name = "bill_type", length = 20)
    private String billType;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "notify_before") // 데이터의 알림 설정값 보존
    private Integer notifyBefore;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public BillingHistory(Long invoiceId, BigDecimal amount, Integer dueDay, String billType, String status, Integer notifyBefore) {
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.dueDay = dueDay;
        this.billType = billType;
        this.status = status;
        this.notifyBefore = notifyBefore;
    }

    public void updateStatus(String status) {
        this.status = status;
    }
}