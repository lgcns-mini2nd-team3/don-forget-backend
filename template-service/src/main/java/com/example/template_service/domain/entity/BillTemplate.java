package com.example.template_service.domain.entity;

import com.example.template_service.domain.enumtype.TemplateCategory;
import jakarta.persistence.*;

@Entity
@Table(name = "bill_template")
public class BillTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id")
    private Long templateId;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TemplateCategory category;

    @Column(name = "is_system", nullable = false)
    private Boolean isSystem;

    protected BillTemplate() {
    }

    public BillTemplate(String name, TemplateCategory category, Boolean isSystem) {
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