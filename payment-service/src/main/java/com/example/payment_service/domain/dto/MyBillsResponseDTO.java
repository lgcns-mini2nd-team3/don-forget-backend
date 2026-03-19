package com.example.payment_service.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MyBillsResponseDTO {
        private Long userInvoiceId;
        private Long userId;
        private Integer issueDay;
        private Integer dueDay;
        private BigDecimal amount;
        private Boolean isRecurring;
        private String recurCycle;      // MONTHLY/BIMONTHLY/QUARTERLY/YEARLY 등
        private LocalDate recurStart;
        private LocalDate recurEnd;
        private LocalDate deletedAt;
}
