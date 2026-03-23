package com.example.payment_service.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.payment_service.domain.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);
    
    // 외부 서비스의 인보이스 ID 목록으로 결제 내역을 일괄 조회하기 위해 추가
    List<Payment> findByInvoiceIdIn(List<Long> invoiceIds);
    
    boolean existsByInvoiceIdAndDueDate(Long invoiceId, LocalDate dueDate);
}