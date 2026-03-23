package com.example.template_service.service;

import com.example.template_service.domain.entity.BillTemplate;
import com.example.template_service.domain.enumtype.TemplateCategory;
import com.example.template_service.dto.request.TemplateCreateRequest;
import com.example.template_service.dto.request.TemplateUpdateRequest;
import com.example.template_service.exception.TemplateNotFoundException;
import com.example.template_service.repository.BillTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateService {

    private final BillTemplateRepository billTemplateRepository;

    public TemplateService(BillTemplateRepository billTemplateRepository) {
        this.billTemplateRepository = billTemplateRepository;
    }

    public List<BillTemplate> getTemplates(String category) {
        if (category == null || category.isBlank()) {
            return billTemplateRepository.findAll();
        }

        TemplateCategory templateCategory = TemplateCategory.valueOf(category.toUpperCase());
        return billTemplateRepository.findByCategory(templateCategory);
    }

    public BillTemplate getTemplate(Long templateId) {
        return findById(templateId);
    }

    public BillTemplate createTemplate(TemplateCreateRequest request) {
        BillTemplate billTemplate = new BillTemplate(
                request.getName(),
                request.getCategory(),
                request.getIsSystem()
        );

        return billTemplateRepository.save(billTemplate);
    }

    public BillTemplate updateTemplate(Long templateId, TemplateUpdateRequest request) {
        BillTemplate billTemplate = findById(templateId);
        billTemplate.update(
                request.getName(),
                request.getCategory(),
                request.getIsSystem()
        );
        return billTemplateRepository.save(billTemplate);
    }

    public void deleteTemplate(Long templateId) {
        BillTemplate billTemplate = findById(templateId);
        billTemplateRepository.delete(billTemplate);
    }

    private BillTemplate findById(Long templateId) {
        return billTemplateRepository.findById(templateId)
                .orElseThrow(() -> new TemplateNotFoundException(templateId));
    }
}