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

    // 1. 일반 등록
    @Transactional
    public void create(Long userId, CreateInvoiceRequest createInvoiceRequest) {
        validateInvoiceRequest(
                createInvoiceRequest.getDueDay(),
                createInvoiceRequest.getNotifyBefore(),
                createInvoiceRequest.getIsRecurring(),
                createInvoiceRequest.getRecurCycle(),
                createInvoiceRequest.getRecurStart()
        );

        InvoiceEntity invoice = createInvoiceRequest.toEntity(userId);
        invoiceRepository.save(invoice);
    }

    /**
     * 2. 외부 연동 등록 (템플릿 및 유저 매핑 반영)
     * 기술적 명분: 외부(Payment) 연동 데이터를 시스템 내 템플릿 정보와 정합성 있게 매핑
     */
    @Transactional
    public void registerExternal(InvoiceResponse dto) {
        // [템플릿 매핑] 외부에서 온 이름(name)을 지현님 시스템의 ID로 변환
        Long mappedTemplateId = matchTemplateId(dto.getName());

        // [유저 매핑] DTO에 유저 식별값(userId)이 있으면 우선 적용, 없으면 기본 유저(1L)
        Long finalUserId = (dto.getUserId() != null && dto.getUserId() != 0L) ? dto.getUserId() : 1L;

        InvoiceEntity entity = InvoiceEntity.builder()
                .userId(finalUserId)
                .templateId(mappedTemplateId) 
                .name(dto.getName() != null ? dto.getName() : "외부 고지서")
                .amount(dto.getAmount())
                .dueDay(dto.getDueDay() != null ? dto.getDueDay() : 1)
                .issueDay(dto.getIssueDay() != null ? dto.getIssueDay() : 1)
                .isRecurring(true) // 외부 연동 데이터는 정기 발행 로직 강제 연동
                .recurCycle(com.example.my_bill_service.enumtype.RecurrenceCycle.MONTHLY)
                .recurStart(dto.getRecurStart() != null ? dto.getRecurStart() : LocalDate.now())
                .notifyBefore(dto.getNotifyBefore() != null ? dto.getNotifyBefore() : 3)
                .build();

        invoiceRepository.save(entity);
    }

    /**
     * 기술적 정합성: 외부 고지서의 이름을 지현님(Template Service)의 DB ID와 매칭
     */
    private Long matchTemplateId(String billName) {
        if (billName == null) return 101L; 
        
        if (billName.contains("전기")) return 1L; 
        if (billName.contains("수도")) return 2L;
        if (billName.contains("가스")) return 3L;
        if (billName.contains("넷플") || billName.contains("구독")) return 4L;
        
        return 101L; 
    }

    // 3. 목록 조회
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getList(Long userId) {
        return invoiceRepository.findAllByUserIdAndDeletedAtIsNull(userId)
                .stream()
                .map(InvoiceResponse::from)
                .collect(Collectors.toList());
    }

    // 4. 단건 조회
    @Transactional(readOnly = true)
    public InvoiceResponse getDetail(Long userId, Long invoiceId) {
        InvoiceEntity invoiceEntity = invoiceRepository.findByIdAndUserIdAndDeletedAtIsNull(invoiceId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "청구서를 찾을 수 없습니다."));
        return InvoiceResponse.from(invoiceEntity);
    }

    // 5. 수정
    @Transactional
    public void update(Long userId, Long invoiceId, UpdateInvoiceRequest updateInvoiceRequest) {
        InvoiceEntity invoiceEntity = invoiceRepository.findByIdAndUserIdAndDeletedAtIsNull(invoiceId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "청구서를 찾을 수 없습니다."));

        validateInvoiceRequest(
            updateInvoiceRequest.getDueDay(),
            updateInvoiceRequest.getNotifyBefore(),
            updateInvoiceRequest.getIsRecurring(),
            updateInvoiceRequest.getRecurCycle(),
            updateInvoiceRequest.getRecurStart()
        );

        invoiceEntity.update(updateInvoiceRequest);
    }

    // 6. 삭제 (soft delete)
    public void delete(Long userId, Long invoiceId) {
        InvoiceEntity invoiceEntity = invoiceRepository.findByIdAndUserIdAndDeletedAtIsNull(invoiceId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "청구서를 찾을 수 없습니다."));

        invoiceEntity.softDelete();
        invoiceRepository.save(invoiceEntity);
    }

    // 7. 알림 대상자 조회
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
                int lastDayOfMonth = today.lengthOfMonth();
                int validDueDay = Math.min(dueDay, lastDayOfMonth);
                LocalDate dueDate = today.withDayOfMonth(validDueDay);
                int remainingDays = (int) java.time.temporal.ChronoUnit.DAYS.between(today, dueDate);

                return NotificationTargetResponse.builder()
                        .userId(invoice.getUserId())
                        .invoiceId(invoice.getId())
                        .notifyBefore(invoice.getNotifyBefore())
                        .dueDate(dueDate)
                        .remainingDays(remainingDays)
                        .name(invoice.getName())
                        .build();
            })
            .collect(Collectors.toList());
    }

    // 8. 발행 대상 조회 (Payment 서비스용)
    public List<InvoiceResponse> getInvoicesByIssueDay(LocalDate today) {
        int todayDay = today.getDayOfMonth();
        int lastDay = today.lengthOfMonth();

        List<InvoiceEntity> invoices;
        if (todayDay == lastDay) {
            invoices = invoiceRepository.findByIssueDayGreaterThanEqualAndDeletedAtIsNull(todayDay);
        } else {
            invoices = invoiceRepository.findByIssueDayAndDeletedAtIsNull(todayDay);
        }

        return invoices.stream()
            .map(InvoiceResponse::from)
            .toList();
    }

    // 공통 검증 로직
    private void validateInvoiceRequest(Integer dueDay, Integer notifyBefore, Boolean isRecurring, Object recurCycle, Object recurStart) {
        if (dueDay == null || dueDay < 1 || dueDay > 31) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "납부일은 1~31 사이여야 합니다.");
        if (notifyBefore == null || notifyBefore < 0) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "알림 기준일은 0 이상이어야 합니다.");
        if (isRecurring == null) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "반복 여부는 필수입니다.");

        if (Boolean.TRUE.equals(isRecurring)) {
            if (recurCycle == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "반복 주기는 필수입니다.");
            if (recurStart == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "반복 시작일은 필수입니다.");
        }
    }
}