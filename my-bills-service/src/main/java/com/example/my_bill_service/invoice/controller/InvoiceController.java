package com.example.my_bill_service.invoice.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_bill_service.invoice.dto.request.CreateInvoiceRequest;
import com.example.my_bill_service.invoice.dto.request.UpdateInvoiceRequest;
import com.example.my_bill_service.invoice.dto.response.CreatePaymentResponse;
import com.example.my_bill_service.invoice.dto.response.InvoiceResponse;
import com.example.my_bill_service.invoice.dto.response.NotificationTargetResponse;
import com.example.my_bill_service.invoice.service.InvoiceService;

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
    @Operation(summary = "청구서 등록", description = "새로운 나의 청구서 등록")
    @PostMapping
    public ResponseEntity<?> createInvoice(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestBody CreateInvoiceRequest createInvoiceRequest
    ) {
        invoiceService.create(userId, createInvoiceRequest);
        return ResponseEntity.ok().build();
    }

    // 목록 조회 
    @Operation(summary = "청구서 목록 조회", description = "사용자의 청구서 목록 조회")
    @GetMapping
    public ResponseEntity<?> getMyBills(
            @RequestHeader("X-USER-ID") Long userId
    ) {
        return ResponseEntity.ok(invoiceService.getList(userId));
    }

    // 단건 조회
    @Operation(summary = "청구서 단건 조회", description = "청구서ID로 단건 조회")
    @GetMapping("/{invoiceId}")
    public ResponseEntity<?> getInvoiceDetail(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable("invoiceId") Long invoiceId
    ) {
        return ResponseEntity.ok(invoiceService.getDetail(userId, invoiceId));
    }

    // update
    @Operation(summary = "청구서 수정", description = "기존 청구서 정보와 반복/알림 규칙 수정")
    @PatchMapping("/{invoiceId}")
    public ResponseEntity<?> updateInvoice(
        @RequestHeader("X-USER-ID") Long userId,
        @PathVariable("invoiceId") Long invoiceId,
        @RequestBody UpdateInvoiceRequest updateInvoiceRequest
    ){
        invoiceService.update(userId, invoiceId, updateInvoiceRequest);
        return ResponseEntity.ok().build();
    }

    // delete
    @Operation(summary = "청구서 삭제", description = "청구서 삭제(soft delete)")
    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<?> deleteInvoice(
        @RequestHeader("X-USER-ID") Long userId,
        @PathVariable("invoiceId") Long invoiceId
    ){
        invoiceService.delete(userId, invoiceId);
        return ResponseEntity.ok().build();
    }

    // 알림 대상자 리스트
    @GetMapping("/internal/notifications/targets")
    public ResponseEntity<?> getNotificationTargets(){
        return ResponseEntity.ok(invoiceService.getNotificationTargets());
    }

    @GetMapping("/issue-targets")
    ResponseEntity<List<CreatePaymentResponse>> getIssueTargets(@RequestParam("issueDay") int issueDay){
        List<CreatePaymentResponse> result = invoiceService.getInvoicesByIssueDay(issueDay);
        return ResponseEntity.ok(result);
    }

    // RequestParam부분은 나중에 X-USER-ID 헤더로 변경 예정
    @GetMapping("/get-invoices")
    ResponseEntity<List<Long>> getInvoicesByUserId(@RequestHeader("X-USER-ID") String userId){

        List<Long> result = invoiceService.getInvoiceIdsByUserId(Long.valueOf(userId));
        return ResponseEntity.ok(result);
    }
    
}