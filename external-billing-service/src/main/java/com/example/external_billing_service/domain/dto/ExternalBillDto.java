package com.example.external_billing_service.domain.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 결제 서비스(Payment Service)와 규격을 맞춘 고지서 데이터 모델
 */
@Data
public class ExternalBillDto {
    private Long invoiceId;    // 고지서 번호
    private BigDecimal amount; // 결제 금액
    private Integer dueDay;    // 납기일
    private String billType;   // 고지서 종류 (ELECTRICITY 등)
}