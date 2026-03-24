package com.example.external_billing_service.service;

import com.example.external_billing_service.client.PaymentClient;
import com.example.external_billing_service.dao.BillingHistoryRepository;
import com.example.external_billing_service.domain.dto.ExternalBillDto;
import com.example.external_billing_service.domain.dto.BillTargetResponse;
import com.example.external_billing_service.domain.entity.BillingHistory;
import com.example.external_billing_service.exception.ExternalBillingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalBillingService {

    private final PaymentClient paymentClient;
    private final BillingHistoryRepository historyRepository;

    /**
     * 기술적 명분: 알림 서비스(Notification) 요청에 따라 영속화된 이력을 규격에 맞춰 반환
     */
    @Transactional(readOnly = true)
    public List<BillTargetResponse> getTargetsForNotification() {
        return historyRepository.findAll().stream()
                .map(history -> BillTargetResponse.builder()
                        .userId(1L) // 기술적 명분: 현재 테스트용 기본 사용자 ID 할당
                        .invoiceId(history.getInvoiceId())
                        .name(history.getName())
                        // 기술적 명분: Integer 타입의 dueDay를 LocalDate 규격으로 매핑
                        .dueDate(LocalDate.now().withDayOfMonth(history.getDueDay() != null ? history.getDueDay() : 1))
                        .notifyBefore(history.getNotifyBefore())
                        .templateId(101L) // 고정된 알림 템플릿 ID
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void forwardToPayment(ExternalBillDto dto) {
        log.info("[데이터 수신] 금액: {}원, 종류: {}", dto.getAmount(), dto.getBillType());
        BillingHistory history = saveInitialHistory(dto);

        try {
            paymentClient.sendToPayment(dto);
            updateHistoryStatus(history, "SUCCESS");
        } catch (Exception e) {
            updateHistoryStatus(history, "FAILED");
            log.error("결제 서비스 호출 실패: {}", e.getMessage());
            throw new ExternalBillingException("결제 서비스 연동 오류: " + e.getMessage());
        }
    }

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

    @Transactional
    public void updateHistoryStatus(BillingHistory history, String status) {
        history.updateStatus(status);
    }

    @Transactional(readOnly = true)
    public List<BillingHistory> getAllHistory() {
        return historyRepository.findAll();
    }
}