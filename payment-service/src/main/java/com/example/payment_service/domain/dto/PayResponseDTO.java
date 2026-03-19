package com.example.payment_service.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.payment_service.domain.entity.Payment;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class PayResponseDTO {
    private Long paymentId;
    private Long invoiceId;
    private String status;
    private BigDecimal amount;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;


    public static PayResponseDTO fromEntity(Payment payment) {
        return PayResponseDTO.builder()
                .paymentId(payment.getPaymentId())
                .invoiceId(payment.getInvoiceId())
                .status(payment.getStatus().name())
                .amount(payment.getAmount())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    // getters/setters (또는 Lombok)   
}
