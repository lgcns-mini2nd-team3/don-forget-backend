package com.example.my_bill_service.ctrl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.my_bill_service.dto.response.InvoiceResponse;
import com.example.my_bill_service.dto.request.CreateInvoiceRequest;
import com.example.my_bill_service.dto.request.UpdateInvoiceRequest;
import com.example.my_bill_service.dto.response.NotificationTargetResponse;
import com.example.my_bill_service.service.InvoiceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/my-bills")
@RequiredArgsConstructor
@Tag(name = "My bill", description = "나의 청구서 관리 API")
public class InvoiceController {

    private final InvoiceService invoiceService;

    // 등록
    @Operation(summary = "청구서 등록", description = "신규 청구서 등록")
    @PostMapping
    public ResponseEntity<?> createInvoice(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestBody CreateInvoiceRequest createInvoiceRequest
    ) {
        invoiceService.create(userId, createInvoiceRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 목록 조회
    @Operation(summary = "청구서 목록 조회", description = "특정 사용자의 청구서 목록 조회")
    @GetMapping
    public ResponseEntity<?> getMyBills(
            @RequestHeader("X-USER-ID") Long userId
    ) {
        return ResponseEntity.ok(invoiceService.getList(userId));
    }

    // 단건 조회
    @Operation(summary = "청구서 단건 조회", description = "청구서 ID 기반 단건 조회")
    @GetMapping("/{invoiceId}")
    public ResponseEntity<?> getInvoiceDetail(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable("invoiceId") Long invoiceId
    ) {
        return ResponseEntity.ok(invoiceService.getDetail(userId, invoiceId));
    }

    // 수정
    @Operation(summary = "청구서 수정", description = "청구서 ID 기반 기존 정보 및 반복/알림 규칙 수정")
    @PatchMapping("/{invoiceId}")
    public ResponseEntity<?> updateInvoice(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable("invoiceId") Long invoiceId,
            @RequestBody UpdateInvoiceRequest updateInvoiceRequest
    ) {
        invoiceService.update(userId, invoiceId, updateInvoiceRequest);
        return ResponseEntity.ok().build();
    }

    // 삭제
    @Operation(summary = "청구서 삭제", description = "청구서 ID 기반 삭제(soft delete)")
    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<?> deleteInvoice(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable("invoiceId") Long invoiceId
    ) {
        invoiceService.delete(userId, invoiceId);
        return ResponseEntity.ok().build();
    }

    // 알림 대상자 리스트
    @Operation(summary = "알림 대상자 목록 조회", description = "알림 발송 대상자 목록 조회")
    @GetMapping("/internal/notifications/targets")
    public ResponseEntity<List<NotificationTargetResponse>> getNotificationTargets() {
        return ResponseEntity.ok(invoiceService.getNotificationTargets());
    }

    // 특정 발행일 기준 청구서 조회
    @Operation(summary = "청구서 발행 대상 조회", description = "특정 issueDay 기준 발행 대상 청구서 조회")
    @GetMapping("/issue-targets")
    public ResponseEntity<List<InvoiceResponse>> getIssueTargets(
            @RequestParam("issueDay") int issueDay
    ) {
        return ResponseEntity.ok(invoiceService.getInvoicesByIssueDay(issueDay));
    }

    // 사용자별 invoice id 조회
    @Operation(summary = "사용자별 청구서 ID 조회", description = "사용자별 청구서 ID 목록 조회")
    @GetMapping("/get-invoices")
    public ResponseEntity<List<Long>> getInvoicesByUserId(
            @RequestParam("userId") Long userId
    ) {
        return ResponseEntity.ok(invoiceService.getInvoiceIdsByUserId(userId));
    }
}