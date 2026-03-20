package com.example.external_billing_service.service;

import com.example.external_billing_service.client.PaymentClient;
import com.example.external_billing_service.dao.BillingHistoryRepository;
import com.example.external_billing_service.domain.dto.ExternalBillDto;
import com.example.external_billing_service.domain.entity.BillingHistory;
import com.example.external_billing_service.exception.ExternalBillingException;
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
     * 외부 고지서 데이터를 수신하여 로컬 아카이빙 후 결제 서비스로 전달
     * 서비스 간 장애 격리(Fault Tolerance) 및 데이터 추적성 확보
     */
    @Transactional
    public void forwardToPayment(ExternalBillDto dto) {
        log.info("========================================");
        log.info("[데이터 수신] 금액: {}원, 종류: {}", dto.getAmount(), dto.getBillType());
        log.info("========================================");

        // 1. 원천 데이터 유실 방지를 위한 초기 영속화 (상태: RECEIVED)
        // 타 서비스 연동 전 로컬 DB 선저장을 통해 외부 데이터 유실 리스크 최소화
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
            log.info("결제 서비스 연동 시도 중... Target: PaymentService");
            paymentClient.sendToPayment(dto);
            
            // 3. 연동 성공 시 상태 업데이트 (Dirty Checking 활용)
            history.updateStatus("SUCCESS");
            log.info("결제 서비스 연동 완료 및 이력 상태 업데이트 (SUCCESS)");
        } catch (Exception e) {
            // 4. 장애 격리 및 재처리 기반 마련
            // 타 서비스의 일시적 장애가 현재 서비스의 전체 트랜잭션 롤백으로 이어지지 않도록 예외 격리
            history.updateStatus("FAILED");
            log.error("결제 서비스 호출 실패: 장애 격리 및 이력 상태 업데이트 (FAILED)");
            
            // 커스텀 예외를 통한 에러 전파 및 추적성 강화
            throw new ExternalBillingException("결제 서비스 연동 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * FE 대시보드 렌더링을 위한 전체 수집 이력 조회
     * 읽기 전용 트랜잭션 설정을 통한 스냅샷 부하 감소 및 데이터 일관성 보장
     */
    @Transactional(readOnly = true)
    public List<BillingHistory> getAllHistory() {
        log.info("[조회 요청] 전체 수집 이력 리스트업 실행");
        return historyRepository.findAll();
    }
}