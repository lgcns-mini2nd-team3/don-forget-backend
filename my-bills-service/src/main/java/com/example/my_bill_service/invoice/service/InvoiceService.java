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

    // 등록
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

    // 목록 조회
    public List<InvoiceEntity> getList(Long userId) {
        return invoiceRepository.findAllByUserIdAndDeletedAtIsNull(userId);
    }

    // 단건 조회
    public InvoiceEntity getDetail(Long userId, Long invoiceId) {
        return invoiceRepository.findByIdAndUserIdAndDeletedAtIsNull(invoiceId, userId)
                .orElseThrow(() -> new RuntimeException("청구서를 찾을 수 없습니다.")); 
    }
}