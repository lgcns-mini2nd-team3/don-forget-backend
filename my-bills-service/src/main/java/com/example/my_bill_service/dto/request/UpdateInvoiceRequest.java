package com.example.my_bill_service.dto.request;

import java.time.LocalDate;

import com.example.my_bill_service.enumtype.RecurrenceCycle;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "청구서 수정 요청 DTO")
public class UpdateInvoiceRequest {

    @Schema(description = "이름", example = "청구서명")
    private String name;
    
    @Schema(description = "금액", example = "480000")
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
}
