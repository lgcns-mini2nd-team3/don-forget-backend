package com.example.external_billing_service.scheduler;

import com.example.external_billing_service.domain.dto.ExternalBillDto;
import com.example.external_billing_service.service.ExternalBillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillingEventScheduler {

    private final ExternalBillingService billingService;
    private final Random random = new Random();
    private final AtomicLong invoiceIdGenerator = new AtomicLong(System.currentTimeMillis());

    // 외부 도메인 API 폴링(Polling) 대체를 위한 주기적 이벤트 스케줄링
    @Scheduled(fixedRate = 60000)
    public void generateAndTransferBillingEvent() {
        // 인바운드 페이로드 생성 로직 (추후 DB 조회 및 실제 외부 API 연동으로 전환 가능)
        long invoiceId = invoiceIdGenerator.incrementAndGet();
        BigDecimal amount = BigDecimal.valueOf((random.nextInt(8) + 3) * 10000);
        int dueDay = random.nextInt(28) + 1;
        String billType = "ELECTRICITY";

        ExternalBillDto eventDto = new ExternalBillDto();
        eventDto.setInvoiceId(invoiceId);
        eventDto.setAmount(amount);
        eventDto.setDueDay(dueDay);
        eventDto.setBillType(billType);

        log.info("========================================");
        log.info("[Event Scheduler] 외부 고지서 수집 이벤트 발생 - InvoiceID: {}", invoiceId);
        log.info("========================================");

        billingService.forwardToPayment(eventDto);
    }
}