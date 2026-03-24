package com.example.notification_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.notification_service.dto.response.BillTargetResponse;

@FeignClient(name = "my-bill-service", url = "http://my-bill-service:80") // my-bill-service의 application.yml에서 설정한 spring.application.name
public interface BillServiceClient {

    @GetMapping("/api/v1/my-bills/internal/notifications/targets")
    List<BillTargetResponse> getNotificationTargets();
}