package com.example.user_service.crtl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_service.domain.dto.UserRequestDTO;
import com.example.user_service.domain.dto.UserResponseDTO;
import com.example.user_service.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyInfo() {
        return ResponseEntity.ok(userService.getMyInfo());
    }

    // 비밀번호 변경
    @PatchMapping("/me/password")
    public ResponseEntity<String> changePassword(@RequestBody UserRequestDTO request) {
        userService.changePassword(request);
        return ResponseEntity.ok("비밀번호 변경 완료");
    }
}
