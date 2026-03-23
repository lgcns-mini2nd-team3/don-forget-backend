package com.example.user_service.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.user_service.dao.UserRepository;
import com.example.user_service.domain.dto.UserRequestDTO;
import com.example.user_service.domain.dto.UserResponseDTO;
import com.example.user_service.domain.entity.UserEntity;
import com.example.user_service.provider.JwtProvider;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Qualifier("redisToken")
    public final RedisTemplate<String, Object> redisTemplate;
    private static final long REFRESH_TOKEN_TTL = 60 * 60 * 24 * 7;

    // Redis에 저장된 refresh token 조회
    public String getRefreshToken(String email){
        Object value = redisTemplate.opsForValue().get("RT:" + email);

        if(value == null){
            throw new RuntimeException("refresh token 없음");
        }

        return value.toString();
    }


    // 회원가입
    public UserResponseDTO signUp(UserRequestDTO request){
        System.out.println(">>>> user service : sign up");

        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException(">>>> 이미 사용중인 이메일입니다.");
        }

        UserEntity entity = UserEntity.builder()
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .name(request.getName())
                                .build();

        UserEntity saveEntity = userRepository.save(entity);

        return UserResponseDTO.fromEntity(saveEntity);
    }


    // 로그인
    public Map<String, Object> signIn(UserRequestDTO request){
        System.out.println(">>>> user service : sign in");
        Map<String, Object> map = new HashMap<>();

        System.out.println(">>>> 1. 사용자 조회");

        UserEntity entity = userRepository.findByEmail(request.getEmail())
                                            .orElseThrow(() -> new RuntimeException(">>>> 사용자 없음"));

        if(!passwordEncoder.matches(request.getPassword(), entity.getPassword())) {
            throw new RuntimeException("비밀번호 일치하지 않음");
        }
        System.out.println(">>>> 2. 토큰 생성");
        String at = jwtProvider.createAt(entity.getEmail(), entity.getUserId());
        String rt = jwtProvider.createRt(entity.getEmail(), entity.getUserId());


        System.out.println(">>>> 3. redis 저장");
        redisTemplate.opsForValue()
                    .set("RT:" + entity.getEmail(), rt, REFRESH_TOKEN_TTL, TimeUnit.SECONDS);
        
        map.put("response", UserResponseDTO.fromEntity(entity));
        map.put("access", at);
        map.put("refresh", rt);

        return map;
        
    }   


    // 로그아웃
    public void logout(String token){
        System.out.println(">>>> user service : logout");

        String email = jwtProvider.getUserEmailFromToken(token);
        String redisKey = "RT:" + email;

        if(Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))){
            redisTemplate.delete(redisKey);
            System.out.println(">>>> refresh token delete complete");
            
            return;
        }
        throw new RuntimeException("Error : can't find refresh token to delete");
        
    }

    // reissue
    public Map<String, Object> reissue(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 refresh token");
        }

        String email = jwtProvider.getUserEmailFromToken(refreshToken);
        Long userId = Long.parseLong(jwtProvider.getUserIdFromToken(refreshToken));
        String storedRt = getRefreshToken(email);

        if (!storedRt.equals(refreshToken)) {
            throw new RuntimeException("유효하지 않은 refresh token");
        }

        String newAt = jwtProvider.createAt(email, userId);

        Map<String, Object> map = new HashMap<>();
        map.put("access", newAt);

        return map;
    }

    // 내 정보 조회
    public UserResponseDTO getMyInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity entity = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        return UserResponseDTO.fromEntity(entity);
    }

    // 비밀번호 변경
    public void changePassword(UserRequestDTO request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity entity = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        entity.changePassword(passwordEncoder.encode(request.getPassword()));
    }
    

}
