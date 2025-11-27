package com.example.spring.common.util.jwt;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.example.spring.common.exception.CustomException;
import com.example.spring.common.exception.ErrorCode;
import com.example.spring.domain.user.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() {
        try {
            this.privateKey = loadPrivateKey();
            this.publicKey = loadPublicKey();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private PrivateKey loadPrivateKey() throws Exception {

        ClassPathResource resource = new ClassPathResource(jwtProperties.getPrivateKeyPath());

        try (InputStream inputStream = resource.getInputStream()) {
            byte[] keyBytes = inputStream.readAllBytes();

            String privateKeyContent = new String(keyBytes, StandardCharsets.UTF_8)
                    .replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(privateKeyContent);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);

        }
    }

    private PublicKey loadPublicKey() throws Exception {
        ClassPathResource resource = new ClassPathResource(jwtProperties.getPublicKeyPath());

        try (InputStream inputStream = resource.getInputStream()) {
            byte[] keyBytes = inputStream.readAllBytes();

            String publiceKeyContent = new String(keyBytes, StandardCharsets.UTF_8)
                    .replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(publiceKeyContent);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);

        }
    }

    // AccessToken: 만료 시간을 짧게
    // 여러 가지 정보를 좀 넣어도 상관 없어요.
    public String createAccessToken(User user) {
        // 토큰의 고유값
        String jwtUuid = UUID.randomUUID().toString();

        // Claims: 토큰에 담을 정보.
        Map<String, Object> claims = new HashMap<>();
        claims.put("Email", user.getEmail());
        claims.put("Name", user.getName());
        claims.put("Role", user.getRole().getName());

        Date now = new Date();

        return Jwts.builder().id(jwtUuid) // JWT 토큰 고유값
                .claims(claims) // 토큰 내부에 제공할 추가 정보
                .subject(user.getEmail()) // 토큰의 주
                .issuedAt(now) // 토근 발급 시간
                .expiration(new Date(now.getTime() + jwtProperties.getAccessTokenExpiry())) // 토큰 만료 시간
                .signWith(privateKey) // 암호화
                .compact();
    }

    // RefreshToken: 만료 시간을 길게
    // 최소한의 정보만 제공.
    public String createRefreshToken(User user) {
        // 토큰의 고유값
        String jwtUuid = UUID.randomUUID().toString();

        // Claims: 토큰에 담을 정보.
        Map<String, Object> claims = new HashMap<>();
        claims.put("Email", user.getEmail());

        Date now = new Date();

        return Jwts.builder().id(jwtUuid) // JWT 토큰 고유값
                .claims(claims) // 토큰 내부에 제공할 추가 정보
                .subject(user.getEmail()) // 토큰의 주
                .issuedAt(now) // 토근 발급 시간
                .expiration(new Date(now.getTime() + jwtProperties.getRefreshTokenExpiry())) // 토큰 만료 시간
                .signWith(privateKey) // 암호화
                .compact();
    }
    
    private Claims extractAllClaims(String token, boolean isAccessToken) {
        
        // AccessToken 이면, Bearer 키워드가 붙어있기 때문에 떼어줘야함.
        if (isAccessToken) {
            token = token.substring(7);
        }

        try {
            Jwts.parser()
                    .verifyWith(publicKey) // private key도 암호화 했으니, public key로 복호화
                    .build()
                    .parseSignedClaims(token) // 토큰의 내용 중 claims을 추출
                    .getPayload(); // 활용할 수 있게 가져옴
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        // 만료 예외 -> 401
        // 조작 예외
        // 형식 예외
        
        return Jwts.parser()
                .verifyWith(publicKey) // private key도 암호화 했으니, public key로 복호화
                .build()
                .parseSignedClaims(token) // 토큰의 내용 중 claims을 추출
                .getPayload(); // 활용할 수 있게 가져옴
    }
    
    public String extractRole(String token, boolean isAccessToken) {
        return extractAllClaims(token, isAccessToken).get("Role", String.class);
    }
    
    public String extractEmail(String token, boolean isAccessToken) {
        return extractAllClaims(token, isAccessToken).get("Email", String.class);
    }
}