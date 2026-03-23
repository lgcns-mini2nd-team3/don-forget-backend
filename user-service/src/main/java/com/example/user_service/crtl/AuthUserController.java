package com.example.user_service.crtl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_service.domain.dto.UserRequestDTO;
import com.example.user_service.domain.dto.UserResponseDTO;
import com.example.user_service.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class AuthUserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> signUp(@RequestBody UserRequestDTO request) {
        return ResponseEntity.ok(userService.signUp(request));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody UserRequestDTO request) {
        return ResponseEntity.ok(userService.signIn(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorization) {
        userService.logout(authorization);
        return ResponseEntity.ok("로그아웃 완료");
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(userService.reissue(request.get("refresh")));
    }
}