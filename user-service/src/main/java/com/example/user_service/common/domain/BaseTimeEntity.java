package com.example.user_service.common.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@MappedSuperclass
@Getter
public class BaseTimeEntity {
    
    @CreationTimestamp
    private LocalDateTime create_at;
    private LocalDateTime deleted_at;;
}
