package com.example.payment_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.payment_service.domain.dto.InvoiceResponse;

import java.util.List;

@FeignClient(name = "my-bill-service") // my-bill-service의 application.yml에서 설정한 spring.application.name
public interface OpenFeignClient {

    // 예: 오늘 발행 대상만 조회 (issueDay=오늘 날짜의 day-of-month)
    @GetMapping("/api/v1/my-bills/issue-targets")
    List<InvoiceResponse> getIssueTargets(
            @RequestParam("issueDay") int issueDay
    );

    @GetMapping("/api/v1/my-bills/get-invoices")
    List<Long> getInvoicesByUserId(@RequestParam("userId") Long userId);
}
