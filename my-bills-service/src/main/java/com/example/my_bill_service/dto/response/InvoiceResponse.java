package com.example.my_bill_service.dto.response;

import java.time.LocalDate;

import com.example.my_bill_service.entity.InvoiceEntity;
import com.example.my_bill_service.enumtype.RecurrenceCycle;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InvoiceResponse {

    private Long invoiceId;
    private Long templateId;
    private Integer amount;
    private Integer dueDay;
    private Integer issueDay;

    private Boolean isRecurring;
    private RecurrenceCycle recurCycle;
    private LocalDate recurStart;
    private LocalDate recurEnd;

    private Integer notifyBefore;

    public static InvoiceResponse from(InvoiceEntity entity) {
        return InvoiceResponse.builder()
                .invoiceId(entity.getId())
                .templateId(entity.getTemplateId())
                .amount(entity.getAmount())
                .dueDay(entity.getDueDay())
                .issueDay(entity.getIssueDay())
                .isRecurring(entity.getIsRecurring())
                .recurCycle(entity.getRecurCycle())
                .recurStart(entity.getRecurStart())
                .recurEnd(entity.getRecurEnd())
                .notifyBefore(entity.getNotifyBefore())
                .build();
    }
}