package com.example.template_service.exception;

public class TemplateNotFoundException extends RuntimeException {
    public TemplateNotFoundException(Long templateId) {
        super("해당 템플릿을 찾을 수 없습니다. templateId=" + templateId);
    }
}