package com.example.my_bill_service.invoice.entity;

import java.time.LocalDate;

import com.example.my_bill_service.global.common.BaseEntity;
import com.example.my_bill_service.invoice.enumtype.RecurrenceCycle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "invoices")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InvoiceEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_invoice_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "template_id", nullable = false)
    private Long templateId;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "due_day", nullable = false)
    private Integer dueDay;

    @Column(name = "is_recurring", nullable = false)
    private Boolean isRecurring;

    @Enumerated(EnumType.STRING)
    @Column(name = "recur_cycle")
    private RecurrenceCycle recurCycle;

    @Column(name = "recur_start")
    private LocalDate recurStart;

    @Column(name = "recur_end")
    private LocalDate recurEnd;

    @Column(name = "notify_before", nullable = false)
    private Integer notifyBefore;

    @Builder
    public InvoiceEntity(Long userId, Long templateId, Integer amount, Integer dueDay,
                   Boolean isRecurring, RecurrenceCycle recurCycle,
                   LocalDate recurStart, LocalDate recurEnd, Integer notifyBefore) {
        this.userId = userId;
        this.templateId = templateId;
        this.amount = amount;
        this.dueDay = dueDay;
        this.isRecurring = isRecurring;
        this.recurCycle = recurCycle;
        this.recurStart = recurStart;
        this.recurEnd = recurEnd;
        this.notifyBefore = notifyBefore;
    }

    public void update(Integer amount, Integer dueDay, Boolean isRecurring,
                       RecurrenceCycle recurCycle, LocalDate recurStart,
                       LocalDate recurEnd, Integer notifyBefore) {
        this.amount = amount;
        this.dueDay = dueDay;
        this.isRecurring = isRecurring;
        this.recurCycle = recurCycle;
        this.recurStart = recurStart;
        this.recurEnd = recurEnd;
        this.notifyBefore = notifyBefore;
    }
}