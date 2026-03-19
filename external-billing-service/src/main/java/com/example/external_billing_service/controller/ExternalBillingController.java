package com.example.external_billing_service.controller;

import com.example.external_billing_service.domain.dto.ExternalBillDto;
import com.example.external_billing_service.service.ExternalBillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/external")
public class ExternalBillingController {

    private final ExternalBillingService billingService;

    /**
     * 외부 시스템으로부터 고지서 데이터를 수신하는 엔드포인트
     * 수신된 데이터를 검증하고 결제 서비스(Payment Service)로 전달하기 위해 서비스 계층 위임
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendBillingData(@RequestBody ExternalBillDto dto) {
        // 기술적 명분: 입력값의 역직렬화(Deserialization) 성공 여부 및 데이터 정합성 육안 검증
        log.info("========================================");
        log.info("[API 호출] 고지서 수집 요청 수신: {}", dto.toString());
        log.info("========================================");

        // 비즈니스 로직 및 타 서비스 전송 처리를 위해 Service 계층으로 전달
        billingService.forwardToPayment(dto);

        return ResponseEntity.ok("Success: Billing data received and delegated to service");
    }
}