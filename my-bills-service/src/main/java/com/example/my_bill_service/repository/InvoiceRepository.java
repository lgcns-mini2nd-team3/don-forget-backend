package com.example.my_bill_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.my_bill_service.entity.InvoiceEntity;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {

    List<InvoiceEntity> findAllByUserIdAndDeletedAtIsNull(Long userId);

    Optional<InvoiceEntity> findByIdAndDeletedAtIsNull(Long id);

    Optional<InvoiceEntity> findByIdAndUserIdAndDeletedAtIsNull(Long id, Long userId);

    List<InvoiceEntity> findAllByDeletedAtIsNull();

    List<InvoiceEntity> findByUserId(Long userId);

    List<InvoiceEntity> findByIssueDayAndDeletedAtIsNull(int issueDay);

    List<InvoiceEntity> findByIssueDayGreaterThanEqualAndDeletedAtIsNull(int issueDay);

}