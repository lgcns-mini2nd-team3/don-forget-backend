package com.example.payment_service.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class InvoiceResponse {

    private Long invoiceId;
    private Long templateId;
    private Long userId;
    // private String invoiceName;
    private BigDecimal amount;
    private Integer dueDay;
    private Integer issueDay;
    private String recurCycle; // RecurrenceCycle enum 대신 String으로 받음 (필요시 변환)
    private Boolean isRecurring;
    private LocalDate recurStart;
    private LocalDate recurEnd;
    private LocalDate deletedAt;

    private Integer notifyBefore;

}