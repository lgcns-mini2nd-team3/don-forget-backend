package com.example.external_billing_service.service;

import com.example.external_billing_service.client.PaymentClient;
import com.example.external_billing_service.dao.BillingHistoryRepository;
import com.example.external_billing_service.domain.dto.ExternalBillDto;
import com.example.external_billing_service.domain.entity.BillingHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalBillingService {

    private final PaymentClient paymentClient;
    private final BillingHistoryRepository historyRepository;

    @Transactional
    public void forwardToPayment(ExternalBillDto dto) {
        // 인바운드 페이로드 정합성 검증용 로깅
        log.info("========================================");
        log.info("[데이터 수신] 금액: {}원, 종류: {}", dto.getAmount(), dto.getBillType());
        log.info("========================================");

        // 원천 데이터 유실 방지 및 재처리 기반 확보를 위한 인바운드 페이로드 영속화
        BillingHistory history = new BillingHistory(
                dto.getInvoiceId(),
                dto.getAmount(),
                dto.getDueDay(),
                dto.getBillType(),
                "RECEIVED"
        );
        historyRepository.save(history);

        try {
            // 선언적 HTTP 클라이언트(Feign)를 통한 결제 도메인 위임
            log.info("결제 서비스 연동 시도 중...");
            paymentClient.sendToPayment(dto);
            
            // 정상 처리 시 영속성 컨텍스트(Dirty Checking)를 통한 상태 업데이트
            history.updateStatus("SUCCESS");
            log.info("결제 서비스 연동 완료 및 이력 상태 업데이트 (SUCCESS)");
        } catch (Exception e) {
            // 타 도메인 장애 격리(Fault Isolation) 및 재처리 대상 마킹
            history.updateStatus("FAILED");
            log.error("결제 서비스 호출 실패: 장애 격리 및 이력 상태 업데이트 (FAILED)");
        }
    }
}