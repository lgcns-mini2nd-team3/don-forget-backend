package com.example.payment_service.domain.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {

    private Long invoiceId;
    private Long userId;
    private Long templateId;
    private String name;
    private Integer amount;
    private Integer dueDay;
    private Integer issueDay;
    private Boolean isRecurring;
    private String recurCycle;
    private LocalDate recurStart;
    private LocalDate recurEnd;
    private Integer notifyBefore;
    private String status;

    // Enum 타입 대신 String으로 수신된 반복 주기를 서비스 로직에서 안전하게 처리하기 위해 추가
    public String getRecurCycleString() {
        return (this.recurCycle != null) ? this.recurCycle.toUpperCase() : "MONTHLY";
    }
}