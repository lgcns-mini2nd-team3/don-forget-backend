package com.example.payment_service.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreatePaymentResponse {
    private Long invoiceId;
    private Long userId;
    private String name;
    private Integer dueDay;
    private BigDecimal amount;
    private Boolean isRecurring;

    private String recurCycle;

    private LocalDate recurStart;

    private LocalDate recurEnd;
}
