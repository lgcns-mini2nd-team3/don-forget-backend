package com.example.payment_service.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private String invoiceName;
    private String status;
    private BigDecimal amount;
    private LocalDate dueDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paidAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;


    public static PayResponseDTO fromEntity(Payment payment) {
        return PayResponseDTO.builder()
                .paymentId(payment.getPaymentId())
                .invoiceName(payment.getInvoiceName())
                .status(payment.getStatus().name())
                .amount(payment.getAmount())
                .dueDate(payment.getDueDate())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    // getters/setters (또는 Lombok)   
}
