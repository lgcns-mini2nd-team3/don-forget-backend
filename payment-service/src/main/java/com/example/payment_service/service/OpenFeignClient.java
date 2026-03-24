package com.example.payment_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; // 추가
import org.springframework.web.bind.annotation.RequestBody; // 추가
import org.springframework.web.bind.annotation.RequestParam;

import com.example.payment_service.domain.dto.InvoiceResponse;

import java.util.List;

@FeignClient(name = "my-bill-service", url = "http://my-bill-service:80") // my-bill-service의 application.yml에서 설정한 spring.application.name
public interface OpenFeignClient {


    // 예: 오늘 발행 대상만 조회 (issueDay=오늘 날짜의 day-of-month)
    // 외부 서비스의 데이터 규격인 InvoiceResponse를 사용하여 발행 대상 목록 수신
    @GetMapping("/api/v1/my-bills/issue-targets")
    List<InvoiceResponse> getIssueTargets(@RequestParam("today") String today);

    // 기술적 명분: 외부 고지서 수집 시 빌 서비스의 invoices 테이블에도 데이터를 동기화하기 위한 API 선언 추가
    @PostMapping("/api/v1/my-bills/external")
    void sendToMyBill(@RequestBody InvoiceResponse dto);
}