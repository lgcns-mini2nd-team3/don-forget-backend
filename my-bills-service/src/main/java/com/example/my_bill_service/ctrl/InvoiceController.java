package com.example.my_bill_service.ctrl;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_bill_service.dto.response.InvoiceResponse;
import com.example.my_bill_service.service.InvoiceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/my-bills")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/issue-targets")
    ResponseEntity<List<InvoiceResponse>> getIssueTargets(@RequestParam("issueDay") int issueDay){
        List<InvoiceResponse> result = invoiceService.getInvoicesByIssueDay(issueDay);
        return ResponseEntity.ok(result);
    }

    // RequestParam부분은 나중에 X-USER-ID 헤더로 변경 예정
    @GetMapping("/get-invoices")
    ResponseEntity<List<Long>> getInvoicesByUserId(@RequestParam("userId") Long userId){
        List<Long> result = invoiceService.getInvoiceIdsByUserId(userId);
        return ResponseEntity.ok(result);
    }
}
