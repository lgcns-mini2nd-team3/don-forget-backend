package com.example.notification_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.notification_service.dto.response.BillTargetResponse;

@FeignClient(name = "bill-service", url = "${feign.bill-service.url}")
public interface BillServiceClient {

    @GetMapping("/api/v1/my-bills/internal/notifications/targets")
    List<BillTargetResponse> getNotificationTargets();
}