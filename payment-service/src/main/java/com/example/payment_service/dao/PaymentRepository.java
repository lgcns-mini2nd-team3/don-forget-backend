package com.example.payment_service.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.payment_service.domain.entity.Payment;
import com.example.payment_service.domain.entity.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // 사용자 ID로 결제 내역 조회
    List<Payment> findByUserId(Long userId);

    // 사용자 ID와 결제 상태로 결제 내역 조회
    List<Payment> findByUserIdAndStatus(Long userId, PaymentStatus status);
    
}