package com.example.user_service.provider;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;





@Component
public class JwtProvider {
    
    
    @Value("${jwt.secret}")
    private String secret;

    private final long ACCESS_TOKEN_EXPIRY = 1000L * 60 * 30;
    private final long REFRESH_TOKEN_EXPIRY = 1000L * 60 * 30 * 24 * 7;


    private Key getStringKey(){
        System.out.println(">>>>> JWT secret : " + secret);
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    //access token
    public String createAt(String email){
        System.out.println(">>>> access token create : " + email);
        return Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                    .signWith(getStringKey())
                    .compact();
    }

    //refresh token
    public String createRt(String email){
        System.out.println(">>>> refresh token create : " + email);
        return Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                    .signWith(getStringKey())
                    .compact();
    }

    // token 추출
    public String getUserEmailFromToken(String token){
        System.out.println(">>>> getUserEmailFromToken token : " + token);
        if(token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        Claims claims = Jwts.parser()
                            .setSigningKey(getStringKey())
                            .parseClaimsJws(token)
                            .getBody();

        return claims.getSubject();
    }


    public long getAte(){
        return ACCESS_TOKEN_EXPIRY;
    }
    public long getRte() {
        return REFRESH_TOKEN_EXPIRY;
    }
}
