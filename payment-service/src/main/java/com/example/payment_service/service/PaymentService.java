package com.example.payment_service.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.payment_service.common.utils.DueDateCalculator;
import com.example.payment_service.dao.PaymentRepository;
import com.example.payment_service.domain.dto.InvoiceResponse;
import com.example.payment_service.domain.dto.PayResponseDTO;
import com.example.payment_service.domain.entity.Payment;
import com.example.payment_service.domain.entity.PaymentStatus;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OpenFeignClient openFeignClient;

    @Transactional(readOnly = true)
    public List<PayResponseDTO> findPaymentsByUser(Long userId) {
        System.out.println("PaymentService: Fetching payments for userId=" + userId);
        List<Long> invoiceIds = openFeignClient.getInvoicesByUserId(userId);
        List<Payment> payments = paymentRepository.findByInvoiceIdIn(invoiceIds);
        return payments.stream()
                .map(PayResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 기술적 명분: PaymentController의 조회 요청 처리를 위한 메서드
    @Transactional(readOnly = true)
    public PayResponseDTO findById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        return PayResponseDTO.fromEntity(payment);
    }

    // 기술적 명분: 결제 완료 상태 업데이트 처리를 위한 메서드
    @Transactional
    public PayResponseDTO markPaid(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        payment.update(PaymentStatus.PAID);
        return PayResponseDTO.fromEntity(payment);  
    }

    // 기술적 명분: 결제 미납/대기 상태 업데이트 처리를 위한 메서드
    @Transactional
    public PayResponseDTO markUnpaid(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        payment.update(PaymentStatus.PENDING);
        return PayResponseDTO.fromEntity(payment);
    }

    // 기술적 명분: 결제 내역 삭제 처리를 위한 메서드
    @Transactional
    public void deleteById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        paymentRepository.delete(payment);
    }

    /**
     * 외부 고지서 데이터 연동 (External Billing 전용)
     */
    @Transactional
    public void registerExternalBilling(Long invoiceId, java.math.BigDecimal amount, LocalDate dueDate) {
        if (paymentRepository.existsByInvoiceIdAndDueDate(invoiceId, dueDate)) {
            return;
        }

        // 기술적 정합성: dev 브랜치 엔티티 생성자 규격 준수 (invoiceId, userId, invoiceName, dueDate, amount)
        Payment payment = new Payment(invoiceId, 0L, "외부 고지서", dueDate, amount);
        paymentRepository.save(payment);
    }
    
    /**
     * 정기 결제 발행 로직 (본인의 정교한 주기 계산 로직 유지)
     */
    @Transactional
    public void issuePaymentsForToday(LocalDate today) {
        int todayDay = today.getDayOfMonth();
        List<InvoiceResponse> targets = openFeignClient.getIssueTargets(today.toString());
        YearMonth ym = YearMonth.from(today);

        for (InvoiceResponse bill : targets) {
            if (!Boolean.TRUE.equals(bill.getIsRecurring())) continue;
            if (bill.getRecurStart() != null && today.isBefore(bill.getRecurStart())) continue;
            if (bill.getRecurEnd() != null && today.isAfter(bill.getRecurEnd())) continue;

            String cycle = bill.getRecurCycleString();
            int cycleMonths = switch (cycle) {
                case "MONTHLY" -> 1;
                case "BIMONTHLY" -> 2;
                case "QUARTERLY" -> 3;
                case "YEARLY" -> 12;
                default -> 0;
            };

            if (cycleMonths == 0 || bill.getRecurStart() == null) continue;

            int monthsDiff = (today.getYear() - bill.getRecurStart().getYear()) * 12
                    + (today.getMonthValue() - bill.getRecurStart().getMonthValue());

            if (monthsDiff < 0 || monthsDiff % cycleMonths != 0) continue;

            int dueDay = bill.getDueDay() == null ? todayDay : bill.getDueDay();
            LocalDate dueDate = DueDateCalculator.calcDueDate(ym, dueDay);

            if (dueDate.isBefore(today)) {
                dueDate = DueDateCalculator.calcDueDate(ym.plusMonths(1), dueDay);
            }

            if (paymentRepository.existsByInvoiceIdAndDueDate(bill.getInvoiceId(), dueDate)) continue;

            Payment payment = new Payment(
                    bill.getInvoiceId(),
                    bill.getUserId(),
                    bill.getName(),
                    dueDate,
                    java.math.BigDecimal.valueOf(bill.getAmount())
            );
            paymentRepository.save(payment);
        }
    }
}