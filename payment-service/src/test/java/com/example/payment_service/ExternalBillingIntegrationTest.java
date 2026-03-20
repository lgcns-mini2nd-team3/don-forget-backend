/**
 * 외부 고지서 수집 데이터 연동 테스트
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
 
    "spring.datasource.url=jdbc:mariadb://localhost:3306/user_bill",
    "spring.datasource.driver-class-name=org.mariadb.jdbc.Driver",
    
    "spring.datasource.username=root",
    //"spring.datasource.password=password", 

    "spring.jpa.hibernate.ddl-auto=update",
    "spring.jpa.database-platform=org.hibernate.dialect.MariaDBDialect",
    "spring.cloud.config.enabled=false"
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

        System.out.println(">>> [MySQL] 연동 테스트 시작: InvoiceID=" + mockInvoiceId);

        // 연동 로직 호출 
        paymentService.registerExternalBilling(mockInvoiceId, mockAmount, mockDueDate);

        System.out.println(">>> [MySQL] 연동 테스트 완료! DB의 payments 테이블을 확인하세요.");
    }
}