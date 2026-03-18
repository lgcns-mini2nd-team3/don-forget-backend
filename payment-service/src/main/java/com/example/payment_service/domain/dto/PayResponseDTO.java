package com.example.payment_service.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.payment_service.domain.entity.Payment;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class PayResponseDTO {
    private Long paymentId;
    private Long userInvoiceId;
    private String status;
    private BigDecimal amount;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;


    public static PayResponseDTO fromEntity(Payment payment) {
        return PayResponseDTO.builder()
                .paymentId(payment.getPaymentId())
                .userInvoiceId(payment.getUserInvoiceId())
                .status(payment.getStatus().name())
                .amount(payment.getAmount())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    // getters/setters (또는 Lombok)   
}
