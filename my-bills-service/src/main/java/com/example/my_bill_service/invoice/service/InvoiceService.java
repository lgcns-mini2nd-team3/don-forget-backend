package com.example.my_bill_service.invoice.service;

import com.example.my_bill_service.invoice.dto.request.CreateInvoiceRequest;
import com.example.my_bill_service.invoice.dto.request.UpdateInvoiceRequest;
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
    public void create(Long userId, CreateInvoiceRequest createInvoiceRequest) {
        InvoiceEntity invoice = InvoiceEntity.builder()
                .userId(userId)
                .templateId(createInvoiceRequest.getTemplateId())
                .amount(createInvoiceRequest.getAmount())
                .dueDay(createInvoiceRequest.getDueDay())
                .isRecurring(createInvoiceRequest.getIsRecurring())
                .recurCycle(createInvoiceRequest.getRecurCycle())
                .recurStart(createInvoiceRequest.getRecurStart())
                .recurEnd(createInvoiceRequest.getRecurEnd())
                .notifyBefore(createInvoiceRequest.getNotifyBefore())
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

    // update
    public void update(Long userId, Long invoiceId, UpdateInvoiceRequest updateInvoiceRequest){
        InvoiceEntity invoiceEntity = invoiceRepository.findByIdAndUserIdAndDeletedAtIsNull(invoiceId, userId)
                                                    .orElseThrow(() -> new RuntimeException("청구서를 찾을 수 없습니다."));
        invoiceEntity.update(
            updateInvoiceRequest.getAmount(), //this.amount = amount;
            updateInvoiceRequest.getDueDay(), //this.dueDay = dueDay;
            updateInvoiceRequest.getIsRecurring(), //this.isRecurring = isRecurring;
            updateInvoiceRequest.getRecurCycle(), //this.recurCycle = recurCycle;
            updateInvoiceRequest.getRecurStart(), //this.recurStart = recurStart;
            updateInvoiceRequest.getRecurEnd(), //this.recurEnd = recurEnd;
            updateInvoiceRequest.getNotifyBefore()
        );

        invoiceRepository.save(invoiceEntity);
    }

    // delete
    public void delete(Long userId, Long invoiceId){
        InvoiceEntity invoiceEntity = invoiceRepository.findByIdAndUserIdAndDeletedAtIsNull(invoiceId, userId)
                                                    .orElseThrow(() -> new RuntimeException("청구서를 찾을 수 없습니다."));

        invoiceEntity.softDelete();
        invoiceRepository.save(invoiceEntity);
    }
}