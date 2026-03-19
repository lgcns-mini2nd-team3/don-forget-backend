package com.example.template_service.dto.response;

import com.example.template_service.domain.entity.BillTemplate;
import com.example.template_service.domain.enumtype.TemplateCategory;

public class TemplateResponse {

    private Long templateId;
    private String name;
    private TemplateCategory category;
    private Boolean isSystem;

    public TemplateResponse(Long templateId, String name, TemplateCategory category, Boolean isSystem) {
        this.templateId = templateId;
        this.name = name;
        this.category = category;
        this.isSystem = isSystem;
    }

    public static TemplateResponse from(BillTemplate billTemplate) {
        return new TemplateResponse(
                billTemplate.getTemplateId(),
                billTemplate.getName(),
                billTemplate.getCategory(),
                billTemplate.getIsSystem()
        );
    }

    public Long getTemplateId() {
        return templateId;
    }

    public String getName() {
        return name;
    }

    public TemplateCategory getCategory() {
        return category;
    }

    public Boolean getIsSystem() {
        return isSystem;
    }
}