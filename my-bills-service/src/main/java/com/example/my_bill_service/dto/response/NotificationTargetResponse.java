package com.example.my_bill_service.dto.response;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "알림 발송 대상 응답 DTO")
public class NotificationTargetResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "청구서 ID", example = "10")
    private Long invoiceId;

    @Schema(description = "이름", example = "청구서명")
    private String name;
    
    @Schema(description = "이번 회차 실제 납부 예정일", example = "2026-03-25")
    private LocalDate dueDate;

    @Schema(description = "알림 기준일(D-n)", example = "3")
    private Integer notifyBefore;

    @Schema(description = "납부일까지 남은 일수(D-day 기준, 0이면 당일, 음수면 지난 상태)", example = "3")
    private Integer remainingDays;
}