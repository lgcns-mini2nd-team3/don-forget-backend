package com.example.my_bill_service.invoice.service;

import com.example.my_bill_service.invoice.dto.request.CreateInvoiceRequest;
import com.example.my_bill_service.invoice.dto.request.UpdateInvoiceRequest;
import com.example.my_bill_service.invoice.entity.InvoiceEntity;
import com.example.my_bill_service.invoice.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    // 등록
    public void create(Long userId, CreateInvoiceRequest createInvoiceRequest) {
        validateInvoiceRequest(
            createInvoiceRequest.getDueDay(),
            createInvoiceRequest.getNotifyBefore(),
            createInvoiceRequest.getIsRecurring(),
            createInvoiceRequest.getRecurCycle(),
            createInvoiceRequest.getRecurStart(),
            createInvoiceRequest.getRecurEnd()
        );

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

        validateInvoiceRequest(
            updateInvoiceRequest.getDueDay(),
            updateInvoiceRequest.getNotifyBefore(),
            updateInvoiceRequest.getIsRecurring(),
            updateInvoiceRequest.getRecurCycle(),
            updateInvoiceRequest.getRecurStart(),
            updateInvoiceRequest.getRecurEnd()
        );


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

    // validate
    private void validateInvoiceRequest(
        Integer dueDay,
        Integer notifyBefore,
        Boolean isRecurring,
        Object recurCycle,
        Object recurStart,
        Object recurEnd
    ){
        if( dueDay == null || dueDay < 1 || dueDay > 31){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "납부일은 1~31 사이여야 합니다.");
        }

        if (notifyBefore == null || notifyBefore < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "알림 기준일은 0 이상이어야 합니다.");
        }

        if (isRecurring == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "반복 여부는 필수입니다.(1: 매월 반복, 0: 일회성)");
        }

        if (Boolean.TRUE.equals(isRecurring)) {
            if (recurCycle == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "반복 주기는 필수입니다.(MONTHLY: 매월, BIMONTHLY: 격월, QUARTERLY: 분기, YEARLY: 연간)");
            }

            if (recurStart == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "반복 시작일은 필수입니다.");
            }
        }
    }
}