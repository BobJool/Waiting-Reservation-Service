package com.bobjool.infrastructure.security;

import com.bobjool.application.interfaces.JwtUtil;
import com.bobjool.application.service.JwtBlacklistService;
import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.domain.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
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
public class JwtUtilImpl implements JwtUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtBlacklistService jwtBlacklistService;
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

    @Override
    public String createAccessToken(User user) {
        return generateCreateToken(user, accessExpiration);
    }

    @Override
    public String createRefreshToken(User user) {
        return generateRefreshToken(user, REFRESH_TOKEN_TIME);
    }


    private String generateCreateToken(User user, long expiration) {
        return BEARER_PREFIX + Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", String.valueOf(user.getId()))
                .claim("role", user.getRole())
                .claim("tokenType", "access")
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    private String generateRefreshToken(User user, long expiration) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("tokenType", "refresh")
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.error("JWT validation error: {}", e.getMessage());
            throw new BobJoolException(ErrorCode.INVALID_TOKEN);
        }
    }

    @Override
    public boolean validateRefreshToken(String refreshToken) {
        try {
            Claims claims = validateAndGetClaims(refreshToken);

            String tokenType = claims.get("tokenType", String.class);
            boolean isRefreshToken = tokenType.equals("refresh");

            if (!isRefreshToken) {
                throw new BobJoolException(ErrorCode.INVALID_TOKEN_TYPE);
            }

            if (jwtBlacklistService.isBlacklisted(refreshToken, isRefreshToken)) {
                throw new BobJoolException(ErrorCode.TOKEN_BLACKLISTED);
            }

            return true;
        } catch (JwtException e) {
            System.err.println("여기2");
            throw new BobJoolException(ErrorCode.INVALID_TOKEN);
        }
    }

    @Override
    public Claims validateAndGetClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.error("JWT claims validation error: {}", e.getMessage());
            throw new BobJoolException(ErrorCode.INVALID_TOKEN);
        }
    }

    @Override
    public String getTokenFromHeader(String headerName, HttpServletRequest request) {
        String header = request.getHeader(headerName);

        if (!StringUtils.hasText(header)) {
            throw new BobJoolException(ErrorCode.TOKEN_MISSING);
        }
        if (!header.startsWith(BEARER_PREFIX)) {
            throw new BobJoolException(ErrorCode.INVALID_TOKEN);
        }

        return header.substring(BEARER_PREFIX.length());
    }

    @Override
    public String getTokenType(String token) {
        Claims claims = validateAndGetClaims(token);
        return claims.get("tokenType", String.class);
    }

    @Override
    public long getRemainingExpiration(String token) {
        Claims claims = validateAndGetClaims(token);
        Date expiration = claims.getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

    @Override
    public List<String> getAllActiveTokens(String username) {
        Set<String> keys = redisTemplate.keys("token:" + username + ":*");

        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }

        return new ArrayList<>(keys);
    }

    @Override
    public String extractUsernameFromRefreshToken(String refreshToken) {
        Claims claims = validateAndGetClaims(refreshToken);

        String tokenType = claims.get("tokenType", String.class);
        if (!"refresh".equals(tokenType)) {
            throw new BobJoolException(ErrorCode.INVALID_TOKEN_TYPE);
        }

        return claims.getSubject();
    }
}
