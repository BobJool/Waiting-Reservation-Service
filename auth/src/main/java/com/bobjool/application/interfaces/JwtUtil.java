package com.bobjool.application.interfaces;

import com.bobjool.domain.entity.User;
import com.bobjool.presentation.dto.response.SignInResDto;
import io.jsonwebtoken.Claims;

import java.util.List;

public interface JwtUtil {
    public SignInResDto createAccessToken(final User user);
    public String createRefreshToken(String username);
    public boolean validateToken(String token);
    public Claims validateAndGetClaims(String token);
    public long getRemainingExpiration(String token);
    public String getTokenFromHeader(String headerName, jakarta.servlet.http.HttpServletRequest request);
    public String getTokenType(String token);
    public List<String> getAllActiveTokens(String username);



}
