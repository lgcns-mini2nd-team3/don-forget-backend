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

    /**
     * 외부 도메인 API 폴링(Polling) 대체를 위한 주기적 이벤트 스케줄링
     * 정당성: 원천 데이터 유실 방지 및 자동 인입(Ingestion) 프로세스 시뮬레이션
     */
    @Scheduled(fixedRate = 60000)
    public void generateAndTransferBillingEvent() {
        try {
            // 인바운드 페이로드 생성 (추후 실제 외부 API 연동으로 전환 가능한 확장성 고려)
            long invoiceId = invoiceIdGenerator.incrementAndGet();
            BigDecimal amount = BigDecimal.valueOf((random.nextInt(8) + 3) * 10000);
            int dueDay = random.nextInt(28) + 1;
            String billType = "ELECTRICITY";

            ExternalBillDto eventDto = new ExternalBillDto();
            eventDto.setInvoiceId(invoiceId);
            eventDto.setAmount(amount);
            eventDto.setDueDay(dueDay);
            eventDto.setBillType(billType);

            // 시각적 가독성 및 모니터링 식별을 위한 로그 포맷 유지
            log.info("========================================");
            log.info("[Event Scheduler] 외부 고지서 수집 이벤트 발생 - InvoiceID: {}", invoiceId);
            log.info("========================================");

            // 서비스 레이어 위임을 통한 연동 프로세스 실행
            billingService.forwardToPayment(eventDto);
            
        } catch (Exception e) {
            // 스케줄링 프로세스 연속성 보장을 위한 예외 핸들링
            log.error("[Scheduler Error] 데이터 인입 중 예외 발생 - Message: {}", e.getMessage());
        }
    }
}