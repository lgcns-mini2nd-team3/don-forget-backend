package com.example.external_billing_service.controller;

import com.example.external_billing_service.domain.dto.ExternalBillDto;
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
@CrossOrigin(origins = "*") // FE(React)와의 교차 출처 자원 공유(CORS) 허용 설정
public class ExternalBillingController {

    private final ExternalBillingService billingService;

    /**
     * 외부 시스템(또는 스케줄러)으로부터 고지서 데이터를 수신하는 엔드포인트
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendBillingData(@RequestBody ExternalBillDto dto) {
        log.info("[API 호출] 고지서 수집 요청 수신: {}", dto.toString());
        billingService.forwardToPayment(dto);
        return ResponseEntity.ok("Success: Billing data received and delegated to service");
    }

    /**
     * 프론트엔드 대시보드 조회를 위한 이력 반환 엔드포인트
     * 기술적 명분: REST API 규격에 따라 수집된 모든 영속 데이터를 리스트 형태로 노출
     */
    @GetMapping("/history")
    public ResponseEntity<List<BillingHistory>> getBillingHistory() {
        log.info("[API 호출] 전체 수집 이력 조회 요청 수신");
        List<BillingHistory> historyList = billingService.getAllHistory();
        return ResponseEntity.ok(historyList);
    }
}