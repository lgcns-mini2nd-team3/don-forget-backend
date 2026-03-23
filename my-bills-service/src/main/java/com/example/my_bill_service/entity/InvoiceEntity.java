package com.example.my_bill_service.entity;

import java.math.BigInteger;
import java.time.LocalDate;

import com.example.my_bill_service.global.common.BaseEntity;
import com.example.my_bill_service.enumtype.RecurrenceCycle;

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
    @Column(name = "invoice_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "template_id", nullable = false)
    private Long templateId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "due_day", nullable = false)
    private Integer dueDay;

    @Column(name = "issue_day", nullable = false)
    private Integer issueDay;

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
    public InvoiceEntity(Long userId, Long templateId, String name, Integer amount, Integer dueDay, Integer issueDay,
                   Boolean isRecurring, RecurrenceCycle recurCycle,
                   LocalDate recurStart, LocalDate recurEnd, Integer notifyBefore) {
        this.userId = userId;
        this.templateId = templateId;
        this.name = name;
        this.amount = amount;
        this.dueDay = dueDay;
        this.issueDay = issueDay;
        this.isRecurring = isRecurring;
        this.recurCycle = recurCycle;
        this.recurStart = recurStart;
        this.recurEnd = recurEnd;
        this.notifyBefore = notifyBefore;
    }

    public void update(String name, Integer amount, Integer dueDay, Integer issueDay, Boolean isRecurring,
                       RecurrenceCycle recurCycle, LocalDate recurStart,
                       LocalDate recurEnd, Integer notifyBefore) {
        this.name = name;
        this.amount = amount;
        this.dueDay = dueDay;
        this.issueDay = issueDay;
        this.isRecurring = isRecurring;
        this.recurCycle = recurCycle;
        this.recurStart = recurStart;
        this.recurEnd = recurEnd;
        this.notifyBefore = notifyBefore;
    }
}