package com.example.payment_service.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.payment_service.common.utils.DueDateCalculator;
import com.example.payment_service.dao.PaymentRepository;
import com.example.payment_service.domain.dto.MyBillsResponseDTO;
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
        List<Payment> payments;
        System.out.println("PaymentService: Fetching payments for userId=" + userId);
        List<Long> invoiceIds = openFeignClient.getInvoicesByUserId(userId);
        System.out.println("PaymentService: Received invoiceIds from my-bill-service: " + invoiceIds);
        payments = paymentRepository.findByUserInvoiceIdIn(invoiceIds);
        return payments.stream()
                .map(PayResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PayResponseDTO findById(long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        return PayResponseDTO.fromEntity(payment);
    }

    @Transactional
    public PayResponseDTO markPaid(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        payment.update(PaymentStatus.PAID);
        return PayResponseDTO.fromEntity(payment);  
    }

    @Transactional
    public PayResponseDTO markUnpaid(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        payment.update(PaymentStatus.PENDING);
        return PayResponseDTO.fromEntity(payment);
    }
    
    @Transactional
    public void issuePaymentsForToday(LocalDate today) {
        int todayDay = today.getDayOfMonth();

        List<MyBillsResponseDTO> targets = openFeignClient.getIssueTargets(todayDay);

        YearMonth ym = YearMonth.from(today);

        for (MyBillsResponseDTO bill : targets) {
            // 1) 기본 유효성/활성 필터 (my-bills에서 이미 걸러줬다면 최소화 가능)
            if (bill.getDeletedAt() != null) continue;
            if (!Boolean.TRUE.equals(bill.getIsRecurring())) continue;

            // recurStart/recurEnd 범위 체크 (정책에 맞게 조절)
            if (bill.getRecurStart() != null && today.isBefore(bill.getRecurStart())) continue;
            if (bill.getRecurEnd() != null && today.isAfter(bill.getRecurEnd())) continue;

            // cycle 체크 (일단 MONTHLY만 처리, 필요하면 확장)
            if (bill.getRecurCycle() != null && !Objects.equals(bill.getRecurCycle(), "MONTHLY")) {
                // TODO: BIMONTHLY/QUARTERLY/YEARLY 정책 확정 후 처리
                continue;
            }

            // 2) 이번 달 납부기한 계산
            int dueDay = bill.getDueDay() == null ? todayDay : bill.getDueDay();
            LocalDate dueDate = DueDateCalculator.calcDueDate(ym, dueDay);

            // (선택) 발행일이 dueDate보다 늦을 때 정책
            // 예: issueDay=25, dueDay=20이면 dueDate가 이미 지났음 → 다음 달로 넘기기
            if (dueDate.isBefore(today)) {
                YearMonth nextYm = ym.plusMonths(1);
                dueDate = DueDateCalculator.calcDueDate(nextYm, dueDay);
            }

            // 3) 중복 생성 방지
            if (paymentRepository.existsByUserInvoiceIdAndDueDate(bill.getUserInvoiceId(), dueDate)) {
                continue;
            }

            // 4) Payment 생성
            Payment payment = new Payment(
                    bill.getUserInvoiceId(),
                    dueDate,
                    bill.getAmount(),        // 변동금액이면 정책에 맞게 0 또는 null
                    PaymentStatus.PENDING
            );

            paymentRepository.save(payment);

            // (선택) Kafka Producer로 PaymentCreated 이벤트 발행 가능
        }
    }
}
