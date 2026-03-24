package com.example.external_billing_service.dao;

import com.example.external_billing_service.domain.entity.BillingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

// 결제 도메인 연동 상태 추적 및 이력 관리를 위한 영속성 인터페이스
public interface BillingHistoryRepository extends JpaRepository<BillingHistory, Long> {
}