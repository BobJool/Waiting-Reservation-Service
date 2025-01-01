package com.bobjool.infrastructure.security;

import com.bobjool.domain.entity.User;
import com.bobjool.presentation.dto.response.SignInResDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.*;

@Slf4j(topic = "JWT 관련 로그")
@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String BEARER_PREFIX = "Bearer ";
    private static final long REFRESH_TOKEN_TIME = 14 * 24 * 60 * 60 * 1000L; // 14 days

    @Value("${service.jwt.secret-key}")
    private String secretKey;

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${service.jwt.access-expiration}")
    private Long accessExpiration;

    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(decodedKey);
    }

    public SignInResDto createAccessToken(final User user) {
        String token = Jwts.builder()
                .setSubject(user.getUsername())
                .claim("tokenType", "access")
                .claim("userId", user.getId())
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .claim("userId", user.getId())
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(key, signatureAlgorithm)
                .compact();

        // Redis에 AccessToken 저장
//        String redisKey = "token:" + user.getUsername() + ":access";
//        redisTemplate.opsForValue().set(redisKey, token, Duration.ofMillis(accessExpiration));

        return SignInResDto.of(BEARER_PREFIX + token);
    }

    public String createRefreshToken(String username) {
        String token = Jwts.builder()
                .setSubject(username)
                .claim("tokenType", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_TIME))
                .signWith(key, signatureAlgorithm)
                .compact();

        // Redis에 RefreshToken 저장
//        String redisKey = "token:" + username + ":refresh";
//        redisTemplate.opsForValue().set(redisKey, token, Duration.ofMillis(REFRESH_TOKEN_TIME));

        return BEARER_PREFIX + token;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.error("유효하지 않은 JWT token: {}", e.getMessage());
        }
        return false;
    }

    public Claims validateAndGetClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.error("유효하지 않은 JWT token: {}", e.getMessage());
            throw e;
        }
    }

    public long getRemainingExpiration(String token) {
        Claims claims = validateAndGetClaims(token);
        Date expiration = claims.getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

    public String getTokenFromHeader(String headerName, jakarta.servlet.http.HttpServletRequest request) {
        String header = request.getHeader(headerName);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    public String getTokenType(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("tokenType", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> getAllActiveTokens(String username) {
        Set<String> keys = redisTemplate.keys("token:" + username + ":*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(keys);
    }
}
