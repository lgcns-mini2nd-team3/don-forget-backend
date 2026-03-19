package com.example.user_service.domain.entity;

import com.example.user_service.common.domain.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseTimeEntity{

    @Id
    private String userId;

    @Column(unique = true, nullable = false)
    private String password;
    
    private String email;
    private String name;
    private String role;
    
}
