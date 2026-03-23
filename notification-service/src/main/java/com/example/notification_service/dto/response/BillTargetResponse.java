package com.example.notification_service.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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