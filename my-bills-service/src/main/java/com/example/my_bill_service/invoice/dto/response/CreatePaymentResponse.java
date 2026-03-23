package com.example.my_bill_service.invoice.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.my_bill_service.invoice.entity.InvoiceEntity;
import com.example.my_bill_service.invoice.enumtype.RecurrenceCycle;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    private RecurrenceCycle recurCycle;

    private LocalDate recurStart;

    private LocalDate recurEnd;

    public static CreatePaymentResponse from(InvoiceEntity entity) {
        CreatePaymentResponse response = new CreatePaymentResponse();
        response.invoiceId = entity.getId();
        response.userId = entity.getUserId();
        response.name = entity.getName();
        response.dueDay = entity.getDueDay();
        response.amount = BigDecimal.valueOf(entity.getAmount());
        response.isRecurring = entity.getIsRecurring();
        response.recurCycle = entity.getRecurCycle();
        response.recurStart = entity.getRecurStart();
        response.recurEnd = entity.getRecurEnd();
        return response;
    }
}
