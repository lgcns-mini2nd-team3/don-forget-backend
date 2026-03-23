package com.example.my_bill_service.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.my_bill_service.dto.request.CreateInvoiceRequest;
import com.example.my_bill_service.dto.request.UpdateInvoiceRequest;
import com.example.my_bill_service.dto.response.InvoiceResponse;
import com.example.my_bill_service.dto.response.NotificationTargetResponse;
import com.example.my_bill_service.entity.InvoiceEntity;
import com.example.my_bill_service.repository.InvoiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    // 등록
    @Transactional
    public void create(Long userId, CreateInvoiceRequest createInvoiceRequest) {
        validateInvoiceRequest(
                createInvoiceRequest.getName(),
                createInvoiceRequest.getDueDay(),
                createInvoiceRequest.getIssueDay(),
                createInvoiceRequest.getNotifyBefore(),
                createInvoiceRequest.getIsRecurring(),
                createInvoiceRequest.getRecurCycle(),
                createInvoiceRequest.getRecurStart(),
                createInvoiceRequest.getRecurEnd()
        );

        InvoiceEntity invoice = createInvoiceRequest.toEntity(userId);

        invoiceRepository.save(invoice);
    }

    // 목록 조회
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getList(Long userId) {
        return invoiceRepository.findAllByUserIdAndDeletedAtIsNull(userId)
                .stream()
                .map(InvoiceResponse::from)
                .collect(Collectors.toList());
    }

    // 단건 조회
    @Transactional(readOnly = true)
    public InvoiceResponse getDetail(Long userId, Long invoiceId) {
        InvoiceEntity invoiceEntity = invoiceRepository.findByIdAndUserIdAndDeletedAtIsNull(invoiceId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "청구서를 찾을 수 없습니다."));
        return InvoiceResponse.from(invoiceEntity);
    }

    // 수정
    @Transactional
    public void update(Long userId, Long invoiceId, UpdateInvoiceRequest updateInvoiceRequest) {
        InvoiceEntity invoiceEntity = invoiceRepository.findByIdAndUserIdAndDeletedAtIsNull(invoiceId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "청구서를 찾을 수 없습니다."));

        validateInvoiceRequest(
            updateInvoiceRequest.getName(),
            updateInvoiceRequest.getDueDay(),
            updateInvoiceRequest.getIssueDay(),
            updateInvoiceRequest.getNotifyBefore(),
            updateInvoiceRequest.getIsRecurring(),
            updateInvoiceRequest.getRecurCycle(),
            updateInvoiceRequest.getRecurStart(),
            updateInvoiceRequest.getRecurEnd()
        );

        invoiceEntity.update(updateInvoiceRequest);
    }

    // 삭제 (soft delete)
    public void delete(Long userId, Long invoiceId) {
        InvoiceEntity invoiceEntity = invoiceRepository.findByIdAndUserIdAndDeletedAtIsNull(invoiceId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "청구서를 찾을 수 없습니다."));

        invoiceEntity.softDelete();
        invoiceRepository.save(invoiceEntity);
    }

    // 발행 대상 조회
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByIssueDay(int issueDay) {
        return invoiceRepository.findByIssueDay(issueDay)
                .stream()
                .map(InvoiceResponse::from)
                .collect(Collectors.toList());
    }

    // 사용자별 invoice id 조회
    @Transactional(readOnly = true)
    public List<Long> getInvoiceIdsByUserId(Long userId) {
        return invoiceRepository.findByUserId(userId)
                .stream()
                .map(InvoiceEntity::getId)
                .collect(Collectors.toList());
    }

    // 알림 대상자 리스트
    @Transactional(readOnly = true)
    public List<NotificationTargetResponse> getNotificationTargets() {
        LocalDate today = LocalDate.now();

        List<InvoiceEntity> invoiceEntities = invoiceRepository.findAllByDeletedAtIsNull();

        return invoiceEntities.stream()
            .filter(invoice -> {
                Integer dueDay = invoice.getDueDay();
                Integer notifyBefore = invoice.getNotifyBefore();

                if (dueDay == null || notifyBefore == null) return false;

                int lastDayOfMonth = today.lengthOfMonth();
                int validDueDay = Math.min(dueDay, lastDayOfMonth);

                LocalDate dueDate = today.withDayOfMonth(validDueDay);

                long remainingDays = java.time.temporal.ChronoUnit.DAYS.between(today, dueDate);

                return remainingDays >= 0 && remainingDays <= notifyBefore;
            })
            .map(invoice -> {
                Integer dueDay = invoice.getDueDay();
                Integer notifyBefore = invoice.getNotifyBefore();

                int lastDayOfMonth = today.lengthOfMonth();
                int validDueDay = Math.min(dueDay, lastDayOfMonth);

                LocalDate dueDate = today.withDayOfMonth(validDueDay);
                int remainingDays = (int) java.time.temporal.ChronoUnit.DAYS.between(today, dueDate);

                return NotificationTargetResponse.builder()
                        .userId(invoice.getUserId())
                        .invoiceId(invoice.getId())
                        .notifyBefore(notifyBefore)
                        .dueDate(dueDate)
                        .remainingDays(remainingDays)
                        .name(invoice.getName())
                        .build();
            })
            .collect(Collectors.toList());
    }

    // 공통 검증  TODO: name, issueday 검증 추가
    private void validateInvoiceRequest(
            String name,
            Integer dueDay,
            Integer issueDay,
            Integer notifyBefore,
            Boolean isRecurring,
            Object recurCycle,
            Object recurStart,
            Object recurEnd
    ) {
        if (dueDay == null || dueDay < 1 || dueDay > 31) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "납부일은 1~31 사이여야 합니다.");
        }

        if (notifyBefore == null || notifyBefore < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "알림 기준일은 0 이상이어야 합니다.");
        }

        if (isRecurring == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "반복 여부는 필수입니다.");
        }

        if (Boolean.TRUE.equals(isRecurring)) {
            if (recurCycle == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "반복 주기는 필수입니다.");
            }

            if (recurStart == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "반복 시작일은 필수입니다.");
            }
        }
    }
}