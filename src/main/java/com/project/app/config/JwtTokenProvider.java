package com.project.app.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtTokenProvider {
	
	/* 스프링 시큐리티와 JWT를 활용하여 사용자 인증 시스템을 구현하는 부분.
	특히 토큰 생성, 인증 정보 추출, 토큰 유효성 검증 등의 기능. 
	이 코드를 통해 사용자가 로그인하면 JWT 토큰이 발급되고, 이후 요청에서는 이 토큰을 통해 사용자를 인증 */
	
    @Value("${jwt.secret}")
    private String secretKey;
    
    private SecretKey key;
    
    private final long tokenValidTime = 30 * 60 * 1000L; // 토큰 유효시간 30분
    
    private final UserDetailsService userDetailsService;
    
    @Autowired
    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    
    @PostConstruct
    protected void init() {
        // 문자열 시크릿 키를 SecretKey 객체로 변환
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
    
    // JWT 토큰 생성
    public String createToken(String userPk, List<String> roles) {
        Claims claims = Jwts.claims().subject(userPk).build();
        claims.put("roles", roles);
        Date now = new Date();
        
        return Jwts.builder()
                .claims(claims) // 정보 저장
                .issuedAt(now) // 토큰 발행시간
                .expiration(new Date(now.getTime() + tokenValidTime)) // 만료시간
                .signWith(key) // 암호화 알고리즘, secret 키
                .compact();
    }
    
    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
    
    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    
    // 토큰 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            var claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}