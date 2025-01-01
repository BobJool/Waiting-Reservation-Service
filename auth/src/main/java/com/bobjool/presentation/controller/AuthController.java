package com.bobjool.presentation.controller;

import com.bobjool.application.service.AuthService;
import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.presentation.dto.request.SignInReqDto;
import com.bobjool.presentation.dto.response.SignInResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ApiResponse<SignInResDto> signIn(
            final @RequestBody SignInReqDto request) {

        final SignInResDto response = authService.signIn(request);

        return new ApiResponse<>(
                HttpStatus.OK,
                HttpStatus.OK.getReasonPhrase(),
                response
        );
    }
}
