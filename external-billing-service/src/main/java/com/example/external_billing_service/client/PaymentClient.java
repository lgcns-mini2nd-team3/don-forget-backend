package com.example.external_billing_service.client;

import com.example.external_billing_service.domain.dto.ExternalBillDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// 결제 도메인 연동을 위한 통신 규격 정의 (유레카 라우팅)
@FeignClient(name = "payment-service", url = "http://payment-service:80") // payment-service의 application.yml에서 설정한 spring.application.name
public interface PaymentClient {
    
    @PostMapping("/api/v1/payments/external")
    String sendToPayment(@RequestBody ExternalBillDto dto);
}