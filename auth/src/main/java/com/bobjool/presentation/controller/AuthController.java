package com.bobjool.presentation.controller;

import com.bobjool.application.service.AuthService;
import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.presentation.dto.request.SignInReqDto;
import com.bobjool.presentation.dto.request.SignUpReqDto;
import com.bobjool.presentation.dto.response.SignInResDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auths")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<SignInResDto>> signIn(
            final @RequestBody SignInReqDto request) {

        final SignInResDto response = authService.signIn(request.toServiceDto());

        return ApiResponse.success(
                SuccessCode.SUCCESS,
                response
        );
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<String>> signUp(
            @RequestBody @Valid SignUpReqDto request) {

        authService.signUp(request.toServiceDto());

        return ApiResponse.success(
                SuccessCode.SUCCESS_INSERT,
                null
        );
    }

    @PostMapping("/sign-out")
    public ResponseEntity<ApiResponse<String>> signOut(
            HttpServletRequest request) {

        authService.signOut(request);

        return ApiResponse.success(
                SuccessCode.SUCCESS,
                null
        );
    }
}
