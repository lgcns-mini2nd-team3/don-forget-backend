package com.example.payment_service.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "payments")
@Getter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    // MSA: 다른 서비스(UserInvoice) 엔티티 참조 X
    // user_invoice_id는 외부 서비스의 식별자 값만 저장
    @Column(name = "user_invoice_id", nullable = false)
    private Long invoiceId;

    // @Column(name = "invoice_name", nullable = false)
    // private String invoiceName;

    @Column(name = "user_id", nullable = false)
    private Long userId;


    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Payment() {}
    // 나중에 필요하면 invoiceName과 userId를 생성자에 추가할 수 있음
    public Payment(Long invoiceId, Long userId, LocalDate dueDate, BigDecimal amount) {
        this.invoiceId = invoiceId;
        this.userId = userId;
        this.dueDate = dueDate;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void update(PaymentStatus newStatus) {
        this.status = newStatus;
        if (newStatus == PaymentStatus.PAID) {
            this.paidAt = LocalDateTime.now();
        } else {
            this.paidAt = null;
        }
    }

    // getters/setters (또는 Lombok)
}