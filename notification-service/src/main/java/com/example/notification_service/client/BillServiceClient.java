package com.example.notification_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.notification_service.dto.BillTargetResponse;

@FeignClient(name = "bill-service", url = "http://localhost:8083")
public interface BillServiceClient {
    
    // 보름님이 만든 경로: /api/v1/my-bills/internal/notifications/targets
    @GetMapping("/api/v1/my-bills/internal/notifications/targets")
    List<BillTargetResponse> getNotificationTargets();
}