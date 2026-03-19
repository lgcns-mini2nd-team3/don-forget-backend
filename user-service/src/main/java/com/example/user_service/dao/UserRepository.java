package com.example.user_service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.user_service.domain.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String>{


}
