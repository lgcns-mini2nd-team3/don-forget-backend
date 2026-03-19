package com.example.my_bill_service.invoice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_bill_service.invoice.dto.request.CreateInvoiceRequest;
import com.example.my_bill_service.invoice.service.InvoiceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/my-bills")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    // 등록
    @PostMapping
    public ResponseEntity<?> createInvoice(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestBody CreateInvoiceRequest request
    ) {
        invoiceService.create(userId, request);
        return ResponseEntity.ok().build();
    }

    // 조회 👈 여기!!
    @GetMapping
    public ResponseEntity<?> getMyBills(
            @RequestHeader("X-USER-ID") Long userId
    ) {
        return ResponseEntity.ok(invoiceService.getList(userId));
    }
}