package com.example.my_bill_service.entity;

import java.math.BigInteger;
import java.time.LocalDate;

import com.example.my_bill_service.dto.request.UpdateInvoiceRequest;
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
import lombok.*;

@Builder
@Getter
@Entity
@Table(name = "invoices")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

    public void update(UpdateInvoiceRequest request) {
        if (request.getName() != null) this.name = request.getName();
        if (request.getAmount() != null) this.amount = request.getAmount();
        if (request.getDueDay() != null) this.dueDay = request.getDueDay();
        if (request.getIssueDay() != null) this.issueDay = request.getIssueDay();
        if (request.getIsRecurring() != null) this.isRecurring = request.getIsRecurring();
        if (request.getRecurCycle() != null) this.recurCycle = request.getRecurCycle();
        if (request.getRecurStart() != null) this.recurStart = request.getRecurStart();
        if (request.getRecurEnd() != null) this.recurEnd = request.getRecurEnd();
        if (request.getNotifyBefore() != null) this.notifyBefore = request.getNotifyBefore();
    }
}