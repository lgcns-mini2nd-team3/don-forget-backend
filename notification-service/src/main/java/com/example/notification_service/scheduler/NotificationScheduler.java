package com.example.notification_service.scheduler;

import java.time.LocalDate;
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

    // @Scheduled(cron = "0 0 9 * * *") // 기존 코드 - 매일 오전 9시에 실행
    @Scheduled(fixedDelay = 10000)      // 테스트 - 10,000ms = 10초마다 실행
    public void run() {
        log.info("⏰ 알림 자동 생성 스케줄러 실행 중...");

        List<BillTargetResponse> invoices = billServiceClient.getNotificationTargets();

        if (invoices == null || invoices.isEmpty()) {
            log.info("📭 오늘은 처리할 청구서가 없습니다.");
            return;
        }

        LocalDate today = LocalDate.now();

        for (BillTargetResponse invoice : invoices) {
            if (invoice.getDueDate() == null) {
                log.warn("청구서 ID {}의 납부일 정보가 없어 건너뜁니다.", invoice.getInvoiceId());
                continue; 
            }

            long remainingDays = java.time.temporal.ChronoUnit.DAYS.between(today, invoice.getDueDate());
            int notifyBefore = invoice.getNotifyBefore();
            
            if (remainingDays >= 0 && remainingDays <= notifyBefore) {
                
                String billName = (invoice.getName() != null && !invoice.getName().isEmpty()) 
                                    ? invoice.getName() 
                                    : "청구서";

                String message = String.format("%s 납부 기한이 %d일 남았습니다! 잊지 말고 확인해 주세요.", billName, remainingDays);
                
                notificationService.createNotification(
                    invoice.getUserId(), 
                    invoice.getInvoiceId(), 
                    "PAYMENT_REMINDER", 
                    message
                );
                
                log.info("✅ 알림 생성 완료: 유저 {}, 항목: {}, 남은 일수: {}", 
                         invoice.getUserId(), billName, remainingDays);
            }
        }
    }
}