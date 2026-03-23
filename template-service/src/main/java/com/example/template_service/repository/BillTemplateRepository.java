package com.example.template_service.repository;

import com.example.template_service.domain.entity.BillTemplate;
import com.example.template_service.domain.enumtype.TemplateCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillTemplateRepository extends JpaRepository<BillTemplate, Long> {
    List<BillTemplate> findByCategory(TemplateCategory category);
}