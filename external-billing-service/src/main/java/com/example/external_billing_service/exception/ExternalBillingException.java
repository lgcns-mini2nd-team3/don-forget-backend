package com.example.external_billing_service.exception;

/**
 * 서비스 전용 커스텀 예외 클래스
 * 외부 연동 및 데이터 인입 오류에 대한 명확한 예외 타입 정의 및 추적성 확보
 */
public class ExternalBillingException extends RuntimeException {
    public ExternalBillingException(String message) {
        super(message);
    }
}