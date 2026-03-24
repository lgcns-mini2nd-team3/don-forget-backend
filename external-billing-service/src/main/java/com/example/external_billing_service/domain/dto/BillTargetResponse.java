package com.example.external_billing_service.domain.dto;

import java.time.LocalDate;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillTargetResponse {
    private Long userId;
    private Long invoiceId;
    private Long templateId;
    private String name;
    private LocalDate dueDate;
    private Integer notifyBefore;
}