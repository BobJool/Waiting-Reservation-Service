package com.bobjool.presentation.controller;

import com.bobjool.application.dto.TokensResDto;
import com.bobjool.application.dto.UserResDto;
import com.bobjool.application.service.AuthService;
import com.bobjool.common.infra.aspect.RequireRole;
import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.presentation.dto.request.SignInReqDto;
import com.bobjool.presentation.dto.request.SignUpReqDto;
import com.bobjool.application.dto.SignInResDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                SuccessCode.SUCCESS_INSERT
        );
    }

    @PostMapping("/sign-out")
    public ResponseEntity<ApiResponse<String>> signOut(
            HttpServletRequest request) {

        authService.signOut(request);

        return ApiResponse.success(
                SuccessCode.SUCCESS
        );
    }

    @RequireRole(value = {"MASTER"})
    @PatchMapping("/{id}/approval")
    public ResponseEntity<ApiResponse<UserResDto>> updateUserApproval(
            @PathVariable Long id,
            @RequestBody Boolean approved
    ) {

        UserResDto response = authService.updateUserApproval(id, approved);

        return ApiResponse.success(
                SuccessCode.SUCCESS_UPDATE,
                response
        );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokensResDto>> refreshAccessToken(
            HttpServletRequest request
    ) {

        TokensResDto tokens = authService.refreshToken(request);

        return ApiResponse.success(
                SuccessCode.SUCCESS,
                tokens
        );
    }
}
