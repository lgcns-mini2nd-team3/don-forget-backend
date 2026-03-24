package com.example.payment_service.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.payment_service.common.utils.DueDateCalculator;
import com.example.payment_service.dao.PaymentRepository;
import com.example.payment_service.dao.BillingHistoryRepository;
import com.example.payment_service.domain.entity.BillingHistory;
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
    private final BillingHistoryRepository billingHistoryRepository;
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

    // 기술적 명분: 결제 완료 상태 업데이트 및 과금 이력(History) 생성을 위한 메서드
    @Transactional
    public PayResponseDTO markPaid(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        
        // 기술적 정합성: 상태 변경과 동시에 영수증(BillingHistory) 데이터를 이력 테이블에 생성
        payment.update(PaymentStatus.PAID);

        BillingHistory history = BillingHistory.builder()
                .invoiceId(payment.getInvoiceId())
                .name(payment.getInvoiceName())
                .amount(payment.getAmount())
                .dueDay(payment.getDueDate().getDayOfMonth())
                .billType("EXTERNAL")
                .status("PAID")
                .notifyBefore(3)
                .build();
                
        billingHistoryRepository.save(history);

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
     * 기술적 명분: 외부 고지서 수집 시 고지서 명칭(invoiceName)을 포함하여 MyBill 서비스의 템플릿 매핑 로직 연동
     */
    @Transactional
    public void registerExternalBilling(Long invoiceId, String invoiceName, java.math.BigDecimal amount, LocalDate dueDate) {
        Long userId = 1L; // 유저 서비스 연동 전 임시 ID

        if (paymentRepository.existsByInvoiceIdAndDueDate(invoiceId, dueDate)) {
            return;
        }

        // 기술적 정합성: 하드코딩된 명칭 대신 실제 고지서 이름(전기요금 등)을 저장하여 데이터 정합성 확보
        Payment payment = new Payment(invoiceId, userId, invoiceName, dueDate, amount);
        paymentRepository.save(payment);

        // MyBill 서비스의 'matchTemplateId' 번역기 로직이 인식할 수 있도록 DTO 구성
        InvoiceResponse syncDto = InvoiceResponse.builder()
                .invoiceId(invoiceId)
                .userId(userId)
                .name(invoiceName) // "전기요금", "수도세" 등의 명칭이 담겨 전송됨
                .amount(amount.intValue())
                .dueDay(dueDate.getDayOfMonth())
                .issueDay(LocalDate.now().getDayOfMonth()) 
                .isRecurring(true) 
                .notifyBefore(3) 
                .status("UNPAID") 
                .recurStart(LocalDate.now()) 
                .build();
        
        openFeignClient.sendToMyBill(syncDto);
    }
    
    /**
     * 정기 결제 발행 로직 (본인의 정교한 주기 계산 로직 유지)
     * 기술적 명분: MyBill 서비스로부터 발행 대상을 조회하여 주기성 결제 데이터를 생성
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