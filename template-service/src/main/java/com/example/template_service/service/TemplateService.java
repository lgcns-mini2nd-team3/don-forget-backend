package com.example.template_service.service;

import com.example.template_service.domain.entity.BillTemplate;
import com.example.template_service.domain.enumtype.TemplateCategory;
import com.example.template_service.dto.request.TemplateCreateRequest;
import com.example.template_service.dto.request.TemplateUpdateRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TemplateService {

    private final List<BillTemplate> templates = new ArrayList<>();

    public TemplateService() {
        templates.add(new BillTemplate(1L, "전기세", TemplateCategory.UTILITY, true));
        templates.add(new BillTemplate(2L, "수도세", TemplateCategory.UTILITY, true));
        templates.add(new BillTemplate(3L, "가스비", TemplateCategory.UTILITY, true));
        templates.add(new BillTemplate(4L, "관리비", TemplateCategory.UTILITY, true));
        templates.add(new BillTemplate(5L, "휴대폰 요금", TemplateCategory.TELECOM, true));
        templates.add(new BillTemplate(6L, "인터넷 요금", TemplateCategory.TELECOM, true));
        templates.add(new BillTemplate(7L, "IPTV", TemplateCategory.TELECOM, true));
        templates.add(new BillTemplate(8L, "건강보험", TemplateCategory.INSURANCE, true));
        templates.add(new BillTemplate(9L, "자동차보험", TemplateCategory.INSURANCE, true));
        templates.add(new BillTemplate(10L, "실손보험", TemplateCategory.INSURANCE, true));
        templates.add(new BillTemplate(11L, "학자금 대출", TemplateCategory.EDUCATION, true));
        templates.add(new BillTemplate(12L, "신용카드 대금", TemplateCategory.CARD_FINANCE, true));
        templates.add(new BillTemplate(13L, "자동차세", TemplateCategory.TRANSPORT, true));
        templates.add(new BillTemplate(14L, "넷플릭스", TemplateCategory.SUBSCRIPTION, true));
        templates.add(new BillTemplate(15L, "유튜브 프리미엄", TemplateCategory.SUBSCRIPTION, true));
    }

    public List<BillTemplate> getTemplates(String category) {
        if (category == null || category.isBlank()) {
            return templates;
        }

        TemplateCategory templateCategory = TemplateCategory.valueOf(category.toUpperCase());

        List<BillTemplate> filteredTemplates = new ArrayList<>();
        for (BillTemplate template : templates) {
            if (template.getCategory() == templateCategory) {
                filteredTemplates.add(template);
            }
        }
        return filteredTemplates;
    }

    public BillTemplate getTemplate(Long templateId) {
        return findById(templateId);
    }

    public BillTemplate createTemplate(TemplateCreateRequest request) {
        Long newId = (long) (templates.size() + 1);

        BillTemplate billTemplate = new BillTemplate(
                newId,
                request.getName(),
                request.getCategory(),
                request.getIsSystem()
        );

        templates.add(billTemplate);
        return billTemplate;
    }

    public BillTemplate updateTemplate(Long templateId, TemplateUpdateRequest request) {
        BillTemplate billTemplate = findById(templateId);
        billTemplate.update(
                request.getName(),
                request.getCategory(),
                request.getIsSystem()
        );
        return billTemplate;
    }

    public void deleteTemplate(Long templateId) {
        BillTemplate billTemplate = findById(templateId);
        templates.remove(billTemplate);
    }

    private BillTemplate findById(Long templateId) {
        for (BillTemplate template : templates) {
            if (template.getTemplateId().equals(templateId)) {
                return template;
            }
        }
        throw new IllegalArgumentException("해당 템플릿을 찾을 수 없습니다. templateId=" + templateId);
    }
}