package com.bobjool.presentation.dto.request;

import com.bobjool.application.dto.SignInDto;
import jakarta.validation.constraints.NotBlank;

public record SignInReqDto(
        @NotBlank(message = "username은(는) 필수 입력값입니다.")
        String username,
        @NotBlank(message = "비밀번호은(는) 필수 입력값입니다.")
        String password
) {

        public SignInDto toServiceDto() {
                return new SignInDto(username, password);
        }
}