package com.bobjool.application.service;

import com.bobjool.application.dto.SignInDto;
import com.bobjool.application.dto.SignUpDto;
import com.bobjool.application.interfaces.JwtUtil;
import com.bobjool.common.exception.*;
import com.bobjool.domain.entity.User;
import com.bobjool.domain.repository.UserRepository;
import com.bobjool.presentation.dto.response.SignInResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
//    private final JwtBlacklistService jwtBlacklistService;
    private final PasswordEncoder passwordEncoder;
    private final ValidationService validationService;

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

        return jwtUtil.createAccessToken(user);
    }

    @Transactional
    public void signUp(final SignUpDto request) {

        validationService.validateDuplicateUsername(request.username());
        validationService.validateDuplicateSlackId(request.slackId());
        validationService.validateDuplicateNickname(request.nickname());
        validationService.validateDuplicateEmail(request.email());
        validationService.validateDuplicatePhoneNumber(request.phoneNumber());

        User user = User.create(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.name(),
                request.nickname(),
                request.email(),
                request.slackId(),
                request.phoneNumber(),
                true,
                request.role()
        );

        userRepository.save(user);
    }
}
