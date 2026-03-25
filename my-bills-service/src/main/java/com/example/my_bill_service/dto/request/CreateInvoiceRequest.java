package com.example.my_bill_service.dto.request;

import java.time.LocalDate;

import com.example.my_bill_service.entity.InvoiceEntity;
import com.example.my_bill_service.enumtype.RecurrenceCycle;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "청구서 생성 요청 DTO")
public class CreateInvoiceRequest {

    @Schema(description = "청구서 템플릿 ID", example = "1")
    private Long templateId;

    @Schema(description = "이름", example = "청구서명")
    private String name;
    
    @Schema(description = "금액", example = "42000")
    private Integer amount;
    
    @Schema(description = "납부일", example = "25")
    private Integer dueDay;

    @Schema(description  = "발행일", example = "20")
    private Integer issueDay;
    
    @Schema(description = "반복 여부", example = "true")
    private Boolean isRecurring;
    
    @Schema(description = "반복 주기", example = "MONTHLY", allowableValues = {"MONTHLY", "BIMONTHLY", "QUARTERLY", "YEARLY"})
    private RecurrenceCycle recurCycle;
    
    @Schema(description = "반복 시작일", example = "2026-03-01")
    private LocalDate recurStart;

    @Schema(description = "반복 종료일", example = "2026-12-31", nullable = true)
    private LocalDate recurEnd;

    @Schema(description = "알림 기준일(D-n)", example = "3")
    private Integer notifyBefore;

    public InvoiceEntity toEntity(Long userId) {
        return InvoiceEntity.builder()
                .userId(userId)
                .templateId(templateId)
                .name(name)
                .amount(amount)
                .dueDay(dueDay)
                .issueDay(issueDay)
                .isRecurring(isRecurring)
                .recurCycle(recurCycle)
                .recurStart(recurStart)
                .recurEnd(recurEnd)
                .notifyBefore(notifyBefore)
                .build();
    }
}