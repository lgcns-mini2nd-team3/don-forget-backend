package com.example.my_bill_service.dto.response;

import java.time.LocalDate;

import org.springframework.cglib.core.Local;

import com.example.my_bill_service.entity.InvoiceEntity;
import com.example.my_bill_service.enumtype.RecurrenceCycle;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "청구서 응답 DTO")
public class InvoiceResponse {

    @Schema(description = "청구서 ID", example = "1")
    private Long invoiceId;

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "템플릿 ID", example = "1")
    private Long templateId;

    @Schema(description = "이름", example = "청구서명")
    private String name;
    
    @Schema(description = "금액", example = "480000")
    private Integer amount;
    
    @Schema(description = "납부일", example = "25")
    private Integer dueDay;
    
    @Schema(description = "발행일", example = "20")
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
    
    @Schema(description = "청구 상태", example = "연체: OVERDUE, UNPAID: 미납, PAID: 납부완료")
    private String status;

    public static InvoiceResponse from(InvoiceEntity entity) {
        return InvoiceResponse.builder()
                .invoiceId(entity.getId())
                .templateId(entity.getTemplateId())
                .name(entity.getName())
                .amount(entity.getAmount())
                .dueDay(entity.getDueDay())
                .issueDay(entity.getIssueDay())
                .isRecurring(entity.getIsRecurring())
                .recurCycle(entity.getRecurCycle())
                .recurStart(entity.getRecurStart())
                .recurEnd(entity.getRecurEnd())
                .notifyBefore(entity.getNotifyBefore())
                .status(calculateStatus(entity))
                .build();
    }

    private static String calculateStatus(InvoiceEntity entity){
        LocalDate today = LocalDate.now();
        int dueDay = entity.getDueDay();

        LocalDate dueDate = LocalDate.of(today.getYear(), today.getMonth(), dueDay);

        if(today.isAfter(dueDate)){
            return "OVERDUE";
        }
        return "UNPAID";
        
    }
}