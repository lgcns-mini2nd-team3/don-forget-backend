/**
 * 외부 고지서 수집 데이터 연동 테스트 파일 (수정본)
 * 지현님(Template) 매핑 로직 및 랜덤 발송 시뮬레이션 검증용
 */
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
    "spring.cloud.config.enabled=false" 
})
class ExternalBillingIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    /**
     * 핵심 수정: 파라미터 개수와 순서를 PaymentService.registerExternalBilling 규격에 맞춤
     */
    @Test
    void 외부고지서_연동_테스트() {
        // 1. 데이터 준비
        Long mockInvoiceId = 999L; // 테스트용 가짜 인보이스 ID 추가
        String mockBillName = "전기요금"; 
        BigDecimal mockAmount = new BigDecimal("55000");
        LocalDate mockDueDate = LocalDate.now().plusDays(7);

        System.out.println(">>> 연동 테스트 시작: 고지서명=" + mockBillName);

        // 2. 서비스 호출 (파라미터 4개: ID, 이름, 금액, 날짜 순서 확인!)
        paymentService.registerExternalBilling(mockInvoiceId, mockBillName, mockAmount, mockDueDate);

        System.out.println(">>> 연동 테스트 완료! MyBill 서비스 DB에서 template_id가 1L로 박혔는지 확인하세요.");
    }

    @Test
    void 결제_완료_상태_업데이트_테스트() {
        Long paymentId = 1L; 
        System.out.println(">>> 결제 완료 테스트 시작: PaymentID=" + paymentId);
        paymentService.markPaid(paymentId);
        System.out.println(">>> 결제 완료 처리 완료!");
    }
}