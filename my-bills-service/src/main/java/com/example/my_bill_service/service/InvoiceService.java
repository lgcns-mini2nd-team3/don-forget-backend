package com.example.my_bill_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.my_bill_service.dto.response.InvoiceResponse;
import com.example.my_bill_service.repository.InvoiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    public List<InvoiceResponse> getInvoicesByIssueDay(int issueDay) {
        List<InvoiceResponse> responses = invoiceRepository.findByIssueDay(issueDay).stream()
                                            .map(InvoiceResponse::from)
                                            .collect(Collectors.toList());
                
        return responses;
    }

    public List<Long> getInvoiceIdsByUserId(Long userId) {
        List<Long> invoiceIds = invoiceRepository.findByUserId(userId).stream()
                                        .map(invoice -> invoice.getId())
                                        .collect(Collectors.toList());
        return invoiceIds;
    }
    


    
}
