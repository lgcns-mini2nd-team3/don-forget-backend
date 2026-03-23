package com.example.my_bill_service.dto.request;

import java.time.LocalDate;

import com.example.my_bill_service.enumtype.RecurrenceCycle;

import lombok.Getter;

@Getter
public class CreateInvoiceRequest {
    private Long templateId;
    private Integer amount;
    private Integer dueDay;
    private Boolean isRecurring;
    private RecurrenceCycle recurCycle;
    private LocalDate recurStart;
    private LocalDate recurEnd;
    private Integer notifyBefore;
}