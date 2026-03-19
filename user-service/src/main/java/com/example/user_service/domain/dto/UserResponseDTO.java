package com.example.user_service.domain.dto;

import com.example.user_service.domain.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    
    private String userId;
    private String email, name, role;

    public UserResponseDTO fromEntity(UserEntity entity){
        return UserResponseDTO.builder()
                                .userId(entity.getUserId())
                                .email(entity.getEmail())
                                .name(entity.getName())
                                .role(entity.getRole())
                                .build();

    }
}
