package com.example.payment_service.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.payment_service.common.utils.DueDateCalculator;
import com.example.payment_service.dao.PaymentRepository;
import com.example.payment_service.domain.dto.CreatePaymentResponse;
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
        payments = paymentRepository.findByUserId(userId);
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
    public PayResponseDTO markPaid(long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        payment.update(PaymentStatus.PAID);
        return PayResponseDTO.fromEntity(payment);  
    }

    @Transactional
    public PayResponseDTO markUnpaid(long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        payment.update(PaymentStatus.PENDING);
        return PayResponseDTO.fromEntity(payment);
    }
    
    @Transactional
    public void issuePaymentsForToday(LocalDate today) {
        int todayDay = today.getDayOfMonth();
        System.out.println("PaymentService: Running issuePaymentsForToday for date: " + today);
        List<CreatePaymentResponse> targets = openFeignClient.getIssueTargets(today.toString());
        System.out.println("PaymentService: Received " + targets.size() + " issue targets from my-bill-service");
        YearMonth ym = YearMonth.from(today);

        for (CreatePaymentResponse  bill : targets) {
            // 1) 기본 유효성/활성 필터링
            if (!Boolean.TRUE.equals(bill.getIsRecurring())) continue;
            System.out.println("PaymentService: Processing issue target: " + bill);
            // recurStart/recurEnd 범위 체크 (정책에 맞게 조절)
            if (bill.getRecurStart() != null && today.isBefore(bill.getRecurStart())) continue;
            if (bill.getRecurEnd() != null && today.isAfter(bill.getRecurEnd())) continue;
            System.out.println("PaymentService: Issue target is valid: " + bill);

            String cycleRaw = bill.getRecurCycle();
            String cycle = (cycleRaw == null) ? "MONTHLY" : cycleRaw.trim().toUpperCase();

            int cycleMonths = switch (cycle) {
                case "MONTHLY" -> 1;
                case "BIMONTHLY" -> 2;
                case "QUARTERLY" -> 3;
                case "YEARLY" -> 12;
                default -> 0;
            };

            if (cycleMonths == 0) {
                System.out.println("PaymentService: Unsupported recurCycle, skipping: [" + cycleRaw + "]");
                continue;
            }

            // recur_start 기준으로 monthsDiff 계산
            LocalDate start = bill.getRecurStart(); // NOT NULL 전제
            int monthsDiff = (today.getYear() - start.getYear()) * 12
                    + (today.getMonthValue() - start.getMonthValue());

            if (monthsDiff < 0) { // start가 미래면 스킵 (이미 위에서 today.isBefore(start)로 거르긴 함)
                continue;
            }

            if (monthsDiff % cycleMonths != 0) {
                System.out.println("PaymentService: Not this cycle month, skipping. cycle=" + cycle + ", monthsDiff=" + monthsDiff);
                continue;
            }

            int dueDay = bill.getDueDay() == null ? todayDay : bill.getDueDay();

            // 1) 이번 달 dueDate 먼저 계산
            LocalDate thisMonthDue = DueDateCalculator.calcDueDate(ym, dueDay); // 출력 : 2024-07-31 (예시)
            System.out.println("PaymentService: thisMonthDue=" + thisMonthDue);

            // 2) 이번 달 건이 이미 있으면 종료 (중요)
            if (paymentRepository.existsByInvoiceIdAndDueDate(bill.getInvoiceId(), thisMonthDue)) {
                continue;
            }

            // 3) dueDate 확정 (지났으면 다음 달로)
            LocalDate dueDate = thisMonthDue; 
            if (dueDate.isBefore(today)) { // 이번 달에 발행되고 다음달에 납부인 경우, payment의 dueDate는 다음 달로 설정
                YearMonth nextYm = ym.plusMonths(1);
                dueDate = DueDateCalculator.calcDueDate(nextYm, dueDay);
                System.out.println("PaymentService: Due date passed, nextMonthDue=" + dueDate);
            }

            // 4) 최종 dueDate도 중복 체크 (안전)
            if (paymentRepository.existsByInvoiceIdAndDueDate(bill.getInvoiceId(), dueDate)) {
                continue;
            }
            
            // 4) Payment 생성
            Payment payment = new Payment(
                    bill.getInvoiceId(),
                    bill.getUserId(),
                    bill.getName(),
                    dueDate,
                    bill.getAmount()
            );
            System.out.println("PaymentService: Creating payment: " + payment);
            paymentRepository.save(payment);

            // (선택) Kafka Producer로 PaymentCreated 이벤트 발행 가능
        }
    }

    @Transactional
    public void deleteById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        paymentRepository.delete(payment);
    }
}
