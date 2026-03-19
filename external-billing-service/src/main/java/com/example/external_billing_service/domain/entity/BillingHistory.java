package com.example.external_billing_service.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// 외부 고지서 원천 데이터 아카이빙 및 재처리 기반 확보용 엔티티
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

    // 연동 상태 관리 (예: RECEIVED, FORWARDED, FAILED)
    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public BillingHistory(Long invoiceId, BigDecimal amount, Integer dueDay, String billType, String status) {
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.dueDay = dueDay;
        this.billType = billType;
        this.status = status;
    }

    public void updateStatus(String status) {
        this.status = status;
    }
}