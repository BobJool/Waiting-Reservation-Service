package com.bobjool.application.interfaces;

import com.bobjool.domain.entity.User;
import com.bobjool.presentation.dto.response.SignInResDto;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface JwtUtil {
    String createAccessToken(User user);
    String createRefreshToken(User user);
    boolean validateToken(String token);
    Claims validateAndGetClaims(String token);
    String getTokenFromHeader(String headerName, HttpServletRequest request);
    String getTokenType(String token);
    long getRemainingExpiration(String token);
    List<String> getAllActiveTokens(String username);
}

