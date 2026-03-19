package com.example.notification_service.dto;

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
    private Long paymentId;
    private String billName; // 고지서 이름 (예: 전기세)
    private int notifyBefore; // D-N일
}