package com.example.my_bill_service.invoice.service;

import com.example.my_bill_service.invoice.dto.request.CreateInvoiceRequest;
import com.example.my_bill_service.invoice.entity.InvoiceEntity;
import com.example.my_bill_service.invoice.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public void create(Long userId, CreateInvoiceRequest request) {
        InvoiceEntity invoice = InvoiceEntity.builder()
                .userId(userId)
                .templateId(request.getTemplateId())
                .amount(request.getAmount())
                .dueDay(request.getDueDay())
                .isRecurring(request.getIsRecurring())
                .recurCycle(request.getRecurCycle())
                .recurStart(request.getRecurStart())
                .recurEnd(request.getRecurEnd())
                .notifyBefore(request.getNotifyBefore())
                .build();

        invoiceRepository.save(invoice);
    }

    public List<InvoiceEntity> getList(Long userId) {
        return invoiceRepository.findAllByUserIdAndDeletedAtIsNull(userId);
    }
}