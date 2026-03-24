/**
 * 외부 고지서 수집 데이터 연동 테스트 파일

package com.example.payment_service;

import com.example.payment_service.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest(classes = com.example.payment_service.PaymentServiceApplication.class)
@TestPropertySource(properties = {
 
    "spring.datasource.url=jdbc:mariadb://localhost:3307/user_bill", 
    "spring.datasource.driver-class-name=org.mariadb.jdbc.Driver",
    "spring.datasource.username=root",

    "spring.datasource.password=1234", 
    "spring.jpa.hibernate.ddl-auto=update",
    "spring.cloud.config.enabled=false" // 테스트 시 컨피그 서버 무시
})
class ExternalBillingIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void 외부고지서_연동_테스트() {
        // 데이터 준비
        Long mockInvoiceId = 101L;
        BigDecimal mockAmount = new BigDecimal("55000");
        LocalDate mockDueDate = LocalDate.now().plusDays(7);

        System.out.println(">>> 연동 테스트 시작: InvoiceID=" + mockInvoiceId);

        // 연동 로직 호출 
        paymentService.registerExternalBilling(mockInvoiceId, mockAmount, mockDueDate);

        System.out.println(">>> 연동 테스트 완료! 테이블을 확인하세요.");
    }

    @Test
    void 결제_완료_상태_업데이트_테스트() {
        // 1. 위 테스트에서 들어간 paymentId가 1번이라고 가정 (DB 캡처 확인 결과 1번 맞음)
        Long paymentId = 1L; 

        System.out.println(">>> 결제 완료 테스트 시작: PaymentID=" + paymentId);

        // 2. 결제 완료 로직 호출 (우리가 수정한 markPaid)
        paymentService.markPaid(paymentId);

        System.out.println(">>> 결제 완료 처리 완료! DB에서 status가 PAID이고 paid_at이 찍혔는지 확인하세요.");
    }
}
     */