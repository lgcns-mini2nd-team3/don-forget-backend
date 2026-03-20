package com.example.notification_service.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.notification_service.client.BillServiceClient;
import com.example.notification_service.dto.response.BillTargetResponse;
import com.example.notification_service.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final BillServiceClient billServiceClient;
    private final NotificationService notificationService;

   // @Scheduled(cron = "0 0 9 * * *") // 기존 코드
    @Scheduled(fixedDelay = 10000)      // 테스트 - 10,000ms = 10초마다 실행
    public void run() {
        log.info("⏰ 알림 자동 생성 스케줄러 실행 중...");

        List<BillTargetResponse> targets = billServiceClient.getNotificationTargets();

        for (BillTargetResponse target : targets) {
            // 임시
            String billName = switch (target.getTemplateId().intValue()) {
                case 1 -> "전기세";
                case 2 -> "수도세";
                case 3 -> "가스비";
                case 4 -> "관리비";
                case 5 -> "휴대폰 요금";
                case 6 -> "인터넷 요금";
                case 7 -> "IPTV";
                case 8 -> "건강보험";
                case 9 -> "자동차보험";
                case 10 -> "실손보험";
                case 11 -> "학자금 대출";
                case 12 -> "신용카드 대금";
                case 13 -> "자동차세";
                case 14 -> "넷플릭스";
                case 15 -> "유튜브 프리미엄";
                default -> "기타 고지서";
            };

            String message = String.format("%s 납부 기한이 %d일 남았습니다! (예정일: %s)", 
                         billName, target.getNotifyBefore(), target.getDueDate());
            
            notificationService.createNotification(
                target.getUserId(),
                target.getInvoiceId(),
                "BEFORE_DUE",
                message
            );
        }
        
        log.info("✅ 총 {}건의 알림이 성공적으로 생성되었습니다.", targets.size());
    }
}