package com.example.payment_service.ctrl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.payment_service.domain.dto.PayResponseDTO;
import com.example.payment_service.domain.entity.PaymentStatus;
import com.example.payment_service.service.PaymentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 특정 사용자의 납부 리스트 조회
     * GET /api/v1/{user_id}/payments
     */
    // @GetMapping("/payments/{user_id}")
    // public ResponseEntity<List<PayResponseDTO>> getPaymentsByUser(
    //         @PathVariable("user_id") Long userId) {
    //     List<PayResponseDTO> result = paymentService.findPaymentsByUser(userId);
        
    //     return ResponseEntity.ok(result);
    // }

    /**
     * 납부 상세 조회
     * GET /api/v1/payments/read/{payment_id}
     */
    @GetMapping("/payments/read/{payment_id}")
    public ResponseEntity<PayResponseDTO> getPayment(@PathVariable("payment_id") Long paymentId) {
        PayResponseDTO result = paymentService.findById(paymentId);
        return ResponseEntity.ok(result);
    }

    /**
     * 체크리스트: 납부 완료 체크
     * PATCH /api/v1/payments/{id}/paid
     * Request body 없음 (서버에서 paidAt=now 처리)
     */
    @PatchMapping("/payments/{id}/paid")
    public ResponseEntity<PayResponseDTO> markPaid(@PathVariable("id") Long paymentId) {
        PayResponseDTO result = paymentService.markPaid(paymentId);
        return ResponseEntity.ok(result);
    }

    /**
     * 체크리스트: 납부 완료 체크 해제
     * PATCH /api/v1/payments/{id}/unpaid
     * Request body 없음 (서버에서 paidAt=null 처리)
     */
    @PatchMapping("/payments/{id}/unpaid")
    public ResponseEntity<PayResponseDTO> markUnpaid(@PathVariable("id") Long paymentId) {
        PayResponseDTO result = paymentService.markUnpaid(paymentId);
        return ResponseEntity.ok(result);
    }
}