package com.example.external_billing_service.domain.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ExternalBillDto {
    private Long invoiceId;
    private BigDecimal amount;
    private Integer dueDay;
    private String billType;
    private Integer notifyBefore; 
}