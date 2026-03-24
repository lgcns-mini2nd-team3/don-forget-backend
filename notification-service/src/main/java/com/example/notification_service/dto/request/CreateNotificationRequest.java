package com.example.notification_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {

    @NotNull(message = "사용자 ID는 필수입니다.")
    @Positive(message = "사용자 ID는 양수여야 합니다.")
    private Long userId;

    @NotBlank(message = "알림 메시지는 비어있을 수 없습니다.")
    private String message;

    @NotNull(message = "관련 결제 ID는 필수입니다.")
    private Long paymentId;
    
    @NotBlank(message = "알림 타입은 필수입니다.")
    private String type;
}