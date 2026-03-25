package com.example.template_service.domain.entity;

import com.example.template_service.domain.enumtype.TemplateCategory;

public class BillTemplate {

    private Long templateId;
    private String name;
    private TemplateCategory category;
    private Boolean isSystem;

    public BillTemplate() {
    }

    public BillTemplate(Long templateId, String name, TemplateCategory category, Boolean isSystem) {
        this.templateId = templateId;
        this.name = name;
        this.category = category;
        this.isSystem = isSystem;
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

    public void update(String name, TemplateCategory category, Boolean isSystem) {
        if (name != null) {
            this.name = name;
        }
        if (category != null) {
            this.category = category;
        }
        if (isSystem != null) {
            this.isSystem = isSystem;
        }
    }
}