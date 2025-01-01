package com.bobjool.application.service;

import com.bobjool.common.exception.*;
import com.bobjool.domain.entity.User;
import com.bobjool.domain.repository.UserRepository;
import com.bobjool.infrastructure.security.JwtUtil;
import com.bobjool.presentation.dto.request.SignInReqDto;
import com.bobjool.presentation.dto.request.SignUpReqDto;
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

    public SignInResDto signIn(SignInReqDto request) {

        String username = request.username();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.password())
        );

        User user = userRepository.findByUsername(username)
                .filter(userInfo -> passwordEncoder.matches(request.password(), userInfo.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("Username을 찾을 수 없습니다."));

        if (user.isDeleted()) {
            throw new BobJoolException(ErrorCode.USER_DELETED);
        }

        if (!user.getIsApproved()) {
            throw new BobJoolException(ErrorCode.USER_NOT_APPROVED);
        }

        return jwtUtil.createAccessToken(user);
    }

    @Transactional
    public void signUp(final SignUpReqDto request) {

        validationService.validateDuplicateUsername(request.getUsername());
        validationService.validateDuplicateSlackId(request.getSlackId());
        validationService.validateDuplicateNickname(request.getNickname());
        validationService.validateDuplicateEmail(request.getEmail());
        validationService.validateDuplicatePhoneNumber(request.getPhoneNumber());

        User user = User.create(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getName(),
                request.getNickname(),
                request.getEmail(),
                request.getSlackId(),
                request.getPhoneNumber(),
                true,
                request.getRole()
        );

        userRepository.save(user);
    }
}
