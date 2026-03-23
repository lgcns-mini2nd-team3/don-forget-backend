package com.example.payment_service.common.scheduler;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.payment_service.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor
public class PaymentIssueScheduler {

    private final PaymentService paymentIssueService;

    // 매일 00:05에 실행 (서버 타임존 주의)
    @Scheduled(cron = "0 22 11 * * *", zone = "Asia/Seoul")
    public void run() {
        paymentIssueService.issuePaymentsForToday(LocalDate.now());
    }
}
