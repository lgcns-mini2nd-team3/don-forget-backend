package com.example.external_billing_service.service;

import com.example.external_billing_service.client.PaymentClient;
import com.example.external_billing_service.dao.BillingHistoryRepository;
import com.example.external_billing_service.domain.dto.ExternalBillDto;
import com.example.external_billing_service.domain.entity.BillingHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalBillingService {

    private final PaymentClient paymentClient;
    private final BillingHistoryRepository historyRepository;

    /**
     * 외부 고지서 데이터를 수신하여 DB에 저장하고 결제 서비스로 전달함
     */
    @Transactional
    public void forwardToPayment(ExternalBillDto dto) {
        log.info("========================================");
        log.info("[데이터 수신] 금액: {}원, 종류: {}", dto.getAmount(), dto.getBillType());
        log.info("========================================");

        // 1. 원천 데이터 유실 방지를 위한 초기 영속화 (상태: RECEIVED)
        BillingHistory history = new BillingHistory(
                dto.getInvoiceId(),
                dto.getAmount(),
                dto.getDueDay(),
                dto.getBillType(),
                "RECEIVED"
        );
        historyRepository.save(history);

        try {
            // 2. FeignClient를 통한 결제 도메인 연동 시도
            log.info("결제 서비스 연동 시도 중...");
            paymentClient.sendToPayment(dto);
            
            // 3. 연동 성공 시 상태 업데이트 (Dirty Checking 활용)
            history.updateStatus("SUCCESS");
            log.info("결제 서비스 연동 완료 및 이력 상태 업데이트 (SUCCESS)");
        } catch (Exception e) {
            // 4. 장애 격리 및 재처리 대상 마킹
            history.updateStatus("FAILED");
            log.error("결제 서비스 호출 실패: 장애 격리 및 이력 상태 업데이트 (FAILED)");
        }
    }

    /**
     * FE 대시보드 렌더링을 위한 전체 수집 이력 조회
     * 기술적 명분: 읽기 전용 트랜잭션 최적화를 통해 조회 성능 향상 및 데이터 일관성 보장
     */
    @Transactional(readOnly = true)
    public List<BillingHistory> getAllHistory() {
        // 최신 데이터 순으로 정렬하여 반환하는 로직을 추가할 수도 있음
        return historyRepository.findAll();
    }
}