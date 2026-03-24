package com.example.external_billing_service.controller;

import com.example.external_billing_service.domain.dto.ExternalBillDto;
import com.example.external_billing_service.domain.dto.BillTargetResponse;
import com.example.external_billing_service.domain.entity.BillingHistory;
import com.example.external_billing_service.service.ExternalBillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/external")
@CrossOrigin(origins = "*")
public class ExternalBillingController {

    private final ExternalBillingService billingService;

    /**
     * 기술적 명분: 외부 고지서 데이터 수신 및 결제 서비스 전송
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendBillingData(@RequestBody ExternalBillDto dto) {
        log.info("[API 호출] 고지서 수집 요청 수신: {}", dto.toString());
        billingService.forwardToPayment(dto);
        return ResponseEntity.ok("Success: Billing data received and delegated to service");
    }

    /**
     * 기술적 명분: 수집된 전체 이력 조회 (FE 대시보드용)
     */
    @GetMapping("/history")
    public ResponseEntity<List<BillingHistory>> getBillingHistory() {
        log.info("[API 호출] 전체 수집 이력 조회 요청 수신");
        List<BillingHistory> historyList = billingService.getAllHistory();
        return ResponseEntity.ok(historyList);
    }

    /**
     * 기술적 명분: notification-service의 FeignClient 연동을 위한 데이터 제공 (Proxy 경로)
     */
    @GetMapping("/targets")
    public ResponseEntity<List<BillTargetResponse>> getTargetsForNotification() {
        log.info("[API 호출] 알림 서비스 전용 데이터 조회 요청 수신");
        List<BillTargetResponse> targets = billingService.getTargetsForNotification();
        return ResponseEntity.ok(targets);
    }
}