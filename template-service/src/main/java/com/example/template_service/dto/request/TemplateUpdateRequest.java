package com.example.template_service.dto.request;

import com.example.template_service.domain.enumtype.TemplateCategory;

public class TemplateUpdateRequest {

    private String name;
    private TemplateCategory category;
    private Boolean isSystem;

    public TemplateUpdateRequest() {
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