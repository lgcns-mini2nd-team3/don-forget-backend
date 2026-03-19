package com.example.template_service.controller;

import com.example.template_service.domain.entity.BillTemplate;
import com.example.template_service.dto.request.TemplateCreateRequest;
import com.example.template_service.dto.request.TemplateUpdateRequest;
import com.example.template_service.dto.response.TemplateResponse;
import com.example.template_service.service.TemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/templates")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    public ResponseEntity<List<TemplateResponse>> getTemplates(
            @RequestParam(required = false) String category
    ) {
        List<TemplateResponse> responses = templateService.getTemplates(category)
                .stream()
                .map(TemplateResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{templateId}")
    public ResponseEntity<TemplateResponse> getTemplate(@PathVariable Long templateId) {
        BillTemplate billTemplate = templateService.getTemplate(templateId);
        return ResponseEntity.ok(TemplateResponse.from(billTemplate));
    }

    @PostMapping
    public ResponseEntity<TemplateResponse> createTemplate(@RequestBody TemplateCreateRequest request) {
        BillTemplate billTemplate = templateService.createTemplate(request);
        return ResponseEntity.ok(TemplateResponse.from(billTemplate));
    }

    @PatchMapping("/{templateId}")
    public ResponseEntity<TemplateResponse> updateTemplate(
            @PathVariable Long templateId,
            @RequestBody TemplateUpdateRequest request
    ) {
        BillTemplate billTemplate = templateService.updateTemplate(templateId, request);
        return ResponseEntity.ok(TemplateResponse.from(billTemplate));
    }

    @DeleteMapping("/{templateId}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long templateId) {
        templateService.deleteTemplate(templateId);
        return ResponseEntity.noContent().build();
    }
}