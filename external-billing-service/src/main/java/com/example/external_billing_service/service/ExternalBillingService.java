package com.example.external_billing_service.service;

import com.example.external_billing_service.client.PaymentClient;
import com.example.external_billing_service.dao.BillingHistoryRepository;
import com.example.external_billing_service.domain.dto.ExternalBillDto;
import com.example.external_billing_service.domain.entity.BillingHistory;
import com.example.external_billing_service.exception.ExternalBillingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalBillingService {

    private final PaymentClient paymentClient;
    private final BillingHistoryRepository historyRepository;

    /**
     * 외부 고지서 수신 후 로컬 아카이빙 및 결제 서비스 전달
     */
    @Transactional
    public void forwardToPayment(ExternalBillDto dto) {
        log.info("========================================");
        log.info("[데이터 수신] 금액: {}원, 종류: {}", dto.getAmount(), dto.getBillType());
        log.info("========================================");

        // 1. 초기 이력 영속화 (상태: RECEIVED)
        // 타 서비스 연동 전 원천 데이터 유실 방지를 위해 별도 트랜잭션으로 저장
        BillingHistory history = saveInitialHistory(dto);

        try {
            // 2. 결제 도메인 연동 시도
            log.info("결제 서비스 연동 시도 중... Target: PaymentService");
            paymentClient.sendToPayment(dto);
            
            // 3. 연동 성공 시 상태 업데이트
            updateHistoryStatus(history, "SUCCESS");
            log.info("결제 서비스 연동 완료 (SUCCESS)");
        } catch (Exception e) {
            // 4. 장애 격리 및 상태 업데이트
            // 외부 서비스 장애가 현재 서비스의 데이터 저장 자체를 롤백시키지 않도록 예외 처리
            updateHistoryStatus(history, "FAILED");
            log.error("결제 서비스 호출 실패: 상태 업데이트 수행 (FAILED)");
            
           throw new ExternalBillingException("결제 서비스 연동 오류: " + e.getMessage());
        }
    }

    /**
     * 원천 데이터 영속화 (별도 트랜잭션 분리)
     * 연동 실패와 무관하게 수신된 원본 데이터를 보존하기 위함
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BillingHistory saveInitialHistory(ExternalBillDto dto) {
        return historyRepository.save(BillingHistory.builder()
                .invoiceId(dto.getInvoiceId())
                .name(dto.getName())
                .amount(dto.getAmount())
                .dueDay(dto.getDueDay())
                .billType(dto.getBillType())
                .notifyBefore(dto.getNotifyBefore()) 
                .status("RECEIVED")
                .build());
    }

    /**
     * 이력 상태 업데이트
     */
    @Transactional
    public void updateHistoryStatus(BillingHistory history, String status) {
        history.updateStatus(status);
    }

    /**
     * 전체 수집 이력 조회
     */
    @Transactional(readOnly = true)
    public List<BillingHistory> getAllHistory() {
        log.info("[조회 요청] 전체 수집 이력 리스트업 실행");
        return historyRepository.findAll();
    }
}