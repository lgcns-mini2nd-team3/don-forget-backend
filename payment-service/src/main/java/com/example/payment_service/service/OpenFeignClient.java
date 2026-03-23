package com.example.payment_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.payment_service.domain.dto.InvoiceResponse;

import java.util.List;

@FeignClient(name = "my-bill-service") // my-bill-service의 application.yml에서 설정한 spring.application.name
public interface OpenFeignClient {

    // 기술적 명분: 특정 사용자의 전체 인보이스 ID 목록을 조회하여 결제 상태를 동기화하기 위해 추가
    @GetMapping("/api/v1/my-bills/user-invoices")
    List<Long> getInvoicesByUserId(@RequestParam("userId") Long userId);

    // 예: 오늘 발행 대상만 조회 (issueDay=오늘 날짜의 day-of-month)
    // 외부 서비스의 데이터 규격인 InvoiceResponse를 사용하여 발행 대상 목록 수신
    @GetMapping("/api/v1/my-bills/issue-targets")
    List<InvoiceResponse> getIssueTargets(@RequestParam("today") String today);
}