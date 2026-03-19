package com.example.payment_service.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.payment_service.domain.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserInvoiceIdIn(List<Long> invoiceIds);
    
    boolean existsByUserInvoiceIdAndDueDate(Long userInvoiceId, LocalDate dueDate);
}