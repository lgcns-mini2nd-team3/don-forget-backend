package com.example.payment_service.ctrl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.payment_service.domain.dto.PayResponseDTO;
import com.example.payment_service.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

@Tag(name = "Payments", description = "납부(결제) 조회/상태 변경 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * [외부 고지서 수집 데이터 수신
     * POST /api/v1/payments/external
     */
    @Operation(
        summary = "외부 고지서 데이터 수신",
        description = "external-billing-service로부터 수집된 고지서 데이터를 전달받아 저장합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "데이터 수신 및 저장 성공")
        }
    )
    @PostMapping("/external")
    public ResponseEntity<String> registerExternal(@RequestBody Map<String, Object> payload) {

        paymentService.registerExternalBilling(
            Long.valueOf(payload.get("billId").toString()),
            "외부고지서",
            new java.math.BigDecimal(payload.get("amount").toString()),
            java.time.LocalDate.parse(payload.get("dueDate").toString())
        );
        return ResponseEntity.ok("External billing data received and registered.");
    }

    /**
     * 특정 사용자의 납부 리스트 조회
     * GET /api/v1/payments/
     */
    @Operation(
        summary = "사용자별 납부 목록 조회",
        description = "특정 사용자의 납부(bill_payment) 목록을 조회합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = PayResponseDTO.class)))
            )
        }
    )
    @GetMapping("/")
    public ResponseEntity<List<PayResponseDTO>> getPaymentsByUser(
            @RequestHeader("X-USER-ID") Long userId) {
        List<PayResponseDTO> result = paymentService.findPaymentsByUser(userId);
        return ResponseEntity.ok(result);
    }

    /**
     * 납부 상세 조회
     * GET /api/v1/payments/read/{payment_id}
     */
    @Operation(
        summary = "납부 상세 조회",
        description = "payment_id로 특정 납부 건 상세를 조회합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(schema = @Schema(implementation = PayResponseDTO.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "납부 건을 찾을 수 없음",
                content = @Content
            )
        }
    )
    @GetMapping("/read/{payment_id}")
    public ResponseEntity<PayResponseDTO> getPayment(@PathVariable("payment_id") Long paymentId) {
        PayResponseDTO result = paymentService.findById(paymentId);
        return ResponseEntity.ok(result);
    }

    /**
     * 체크리스트: 납부 완료 체크
     * PATCH /api/v1/payments/{payment_id}/paid
     */
    @Operation(
        summary = "납부 완료 처리",
        description = "납부 상태를 PAID로 변경하고 paidAt을 현재 시각으로 기록합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "처리 성공",
                content = @Content(schema = @Schema(implementation = PayResponseDTO.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "납부 건을 찾을 수 없음",
                content = @Content
            )
        }
    )
    @PatchMapping("/{payment_id}/paid")
    public ResponseEntity<PayResponseDTO> markPaid(@PathVariable("payment_id") Long paymentId) {
        PayResponseDTO result = paymentService.markPaid(paymentId);
        return ResponseEntity.ok(result);
    }

    /**
     * 체크리스트: 납부 완료 체크 해제
     * PATCH /api/v1/payments/{payment_id}/unpaid
     */
    @Operation(
        summary = "납부 완료 해제",
        description = "납부 상태를 PENDING으로 되돌리고 paidAt을 null 처리합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "처리 성공",
                content = @Content(schema = @Schema(implementation = PayResponseDTO.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "납부 건을 찾을 수 없음",
                content = @Content
            )
        }
    )
    @PatchMapping("/{payment_id}/unpaid")
    public ResponseEntity<PayResponseDTO> markUnpaid(@PathVariable("payment_id") Long paymentId) {
        PayResponseDTO result = paymentService.markUnpaid(paymentId);
        return ResponseEntity.ok(result);
    }

    /**
    * 체크리스트: 납부 건 삭제
    * DELETE /api/v1/payments/{payment_id}/delete
    */
    @Operation(
        summary = "납부 건 삭제",
        description = "특정 납부 건을 삭제합니다.",
        responses = {
            @ApiResponse(
                responseCode = "204",
                description = "삭제 성공",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "404",
                description = "납부 건을 찾을 수 없음",
                content = @Content
            )
        }
    )
    @DeleteMapping("/{payment_id}/delete")
    public ResponseEntity<Void> deletePayment(@PathVariable("payment_id") Long paymentId) {
        paymentService.deleteById(paymentId);
        return ResponseEntity.noContent().build();
    }
}