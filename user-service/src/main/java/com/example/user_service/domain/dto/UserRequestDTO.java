package com.example.user_service.domain.dto;

import com.example.user_service.domain.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {
    
    private String email;
    private String password;
    private String name;

    public UserEntity toEntity(){
        return UserEntity.builder()
                        .email(this.email)
                        .password(this.password)
                        .name(this.name)
                        .build();
    }
}
