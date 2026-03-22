package com.example.payment_service.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.payment_service.domain.entity.RecurrenceCycle;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreatePaymentResponse {
    private Long invoiceId;
    private Long userId;
    private Integer dueDay;
    private BigDecimal amount;
    private Boolean isRecurring;

    @Enumerated(EnumType.STRING)
    private RecurrenceCycle recurCycle;

    private LocalDate recurStart;

    private LocalDate recurEnd;
}
