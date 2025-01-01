package com.bobjool.infrastructure.security;

import com.bobjool.application.service.JwtBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "JWT 인증 필터 관련 로그")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtBlacklistService jwtBlacklistService;
    private final CustomUserDetailsServiceImpl customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String requestUri = request.getRequestURI();
            log.info("requestUri: {}", requestUri);
            if (isExcludedUri(requestUri)) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = getTokenFromHeader(request);

            if (token == null || !jwtUtil.validateToken(token)) {
                handleError(response, HttpStatus.UNAUTHORIZED, "유효하지 않거나 누락된 토큰입니다.");
                return;
            }

            if (StringUtils.hasText(token)) {

                String tokenType = jwtUtil.getTokenType(token);
                if (tokenType == null) {
                    handleError(response, HttpStatus.BAD_REQUEST, "Invalid or missing token type");
                    return;
                }

                boolean isAccessToken = "access".equals(tokenType);
                if (jwtBlacklistService.isBlacklisted(token, isAccessToken)) {
                    handleError(response, HttpStatus.UNAUTHORIZED, "Token is blacklisted");
                    return;
                }

                Claims claims = jwtUtil.validateAndGetClaims(token);
                setAuthentication(claims.getSubject());
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT token", e);
            handleError(response, HttpStatus.UNAUTHORIZED, "만료된 Token");
        } catch (JwtException e) {
            log.error("유효하지 않은 JWT token: {}", e.getMessage());
            handleError(response, HttpStatus.UNAUTHORIZED, "유효하지 않은 token");
        } catch (Exception e) {
            log.error("예기치 않은 오류가 발생했습니다.", e);
            handleError(response, HttpStatus.INTERNAL_SERVER_ERROR, "예기치 않은 오류가 발생했습니다.");
        }
    }

    private String getTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return (StringUtils.hasText(header) && header.startsWith("Bearer ")) ? header.substring(7) : null;
    }

    private void setAuthentication(String username) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("Authentication set for user: {}", username);
    }

    private void handleError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(String.format("{\"code\":%d, \"message\":\"%s\"}", status.value(), message));
    }

    private boolean isExcludedUri(String uri) {
        return uri.startsWith("/api/v1/auths/sign-in") || uri.startsWith("/api/v1/auths/sign-up");
    }

}
