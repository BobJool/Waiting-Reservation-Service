package com.bobjool.application.service;

import com.bobjool.application.client.NotificationClient;
import com.bobjool.application.dto.*;
import com.bobjool.application.interfaces.JwtUtil;
import com.bobjool.common.exception.*;
import com.bobjool.domain.entity.User;
import com.bobjool.domain.repository.UserRepository;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final JwtBlacklistService jwtBlacklistService;
    private final PasswordEncoder passwordEncoder;
    private final ValidationService validationService;
    private final NotificationClient notificationClient;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "token:refresh:%s";

    public SignInResDto signIn(SignInDto request) {

        String username = request.username();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.password())
        );

        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .filter(userInfo -> passwordEncoder.matches(request.password(), userInfo.getPassword()))
                .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

        if (!user.getIsApproved()) {
            throw new BobJoolException(ErrorCode.USER_NOT_APPROVED);
        }

        String accessToken = jwtUtil.createAccessToken(user);
        String refreshToken = jwtUtil.createRefreshToken(user);

        // 레디스에 리프레시 토큰 저장
        String redisKey = String.format(REFRESH_TOKEN_PREFIX, user.getUsername());

        redisTemplate.opsForValue().set(redisKey, refreshToken, 14, TimeUnit.DAYS);

        return SignInResDto.from(accessToken, refreshToken);
    }

    @Transactional
    public void signUp(final SignUpDto request) {

        validationService.validateDuplicateUsername(request.username());
        validationService.validateDuplicateSlackEmail(request.slackEmail());
        validationService.validateDuplicateNickname(request.nickname());
        validationService.validateDuplicateEmail(request.email());
        validationService.validateDuplicatePhoneNumber(request.phoneNumber());

        String slackEmail = request.slackEmail();
        String slackId = "";

        if (slackEmail != null && !slackEmail.isEmpty()) {
            try {
                slackId = notificationClient.findSlackIdByEmail(slackEmail).data().get("slack_id");
            } catch (FeignException e) {
                throw new BobJoolException(ErrorCode.INVALID_SLACK_EMAIL);
            }
        }

        User user = User.create(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.name(),
                request.nickname(),
                request.email(),
                request.slackEmail(),
                slackId,
                request.phoneNumber(),
                request.role()
        );

        userRepository.save(user);
    }

    public void signOut(HttpServletRequest request) {
        validateAndBlacklistToken(request);
    }

    @Transactional
    public UserResDto updateUserApproval(Long id, Boolean approved) {

        User user = findUserById(id);

        if (!user.isOwner()) {
            throw new BobJoolException(ErrorCode.MISSING_OWNER_ROLE);
        }

        user.updateUserApproval(approved);

        return UserResDto.from(user);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BobJoolException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public TokensResDto refreshToken(HttpServletRequest request) {

        String refreshToken = jwtUtil.getTokenFromHeader("Authorization", request);

        if (refreshToken == null || !jwtUtil.validateRefreshToken(refreshToken)) {
            throw new BobJoolException(ErrorCode.INVALID_TOKEN);
        }

        String username = jwtUtil.extractUsernameFromRefreshToken(refreshToken);
        String redisKey = String.format(REFRESH_TOKEN_PREFIX, username);
        String storedToken = (String) redisTemplate.opsForValue().get(redisKey);

        // 레디스의 리프레시 토큰과 비교 검증
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new BobJoolException(ErrorCode.INVALID_TOKEN);
        }

        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

        // 이전 리프레시 토큰 블랙 리스트에 추가
        validateAndBlacklistToken(request);

        // 새로운 토큰
        String newAccessToken = jwtUtil.createAccessToken(user);
        String newRefreshToken = jwtUtil.createRefreshToken(user);

        // 레디스에 새 리프레시 토큰 저장
        redisTemplate.opsForValue().set(redisKey, newRefreshToken, 14, TimeUnit.DAYS);

        return new TokensResDto(newAccessToken, newRefreshToken);
    }

    private void validateAndBlacklistToken(HttpServletRequest request) {

        String token = jwtUtil.getTokenFromHeader("Authorization", request);

        if (token == null || !jwtUtil.validateRefreshToken(token)) {

            throw new BobJoolException(ErrorCode.INVALID_TOKEN);
        }

        String tokenType = jwtUtil.getTokenType(token);
        long expiration = jwtUtil.getRemainingExpiration(token);

        boolean isRefreshToken = "refresh".equals(tokenType);

        // 블랙리스트에 추가
        jwtBlacklistService.addToBlacklist(token, expiration, isRefreshToken);
    }
}