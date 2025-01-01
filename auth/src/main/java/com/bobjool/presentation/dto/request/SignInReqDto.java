package com.bobjool.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record SignInReqDto(
        @NotNull(message = "username은(는) 필수 입력값입니다.")
        String username,
        @NotNull(message = "비밀번호은(는) 필수 입력값입니다.")
        String password
) {
}

