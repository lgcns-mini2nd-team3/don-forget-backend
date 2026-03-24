package com.example.external_billing_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

// 배치 작업 및 주기적 데이터 생성을 위한 스케줄링 인프라 활성화
@Configuration
@EnableScheduling
public class SchedulerConfig {
}