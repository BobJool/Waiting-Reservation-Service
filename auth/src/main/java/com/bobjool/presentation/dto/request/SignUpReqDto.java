package com.bobjool.presentation.dto.request;

import com.bobjool.domain.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpReqDto {

    @NotBlank(message = "ID는 비워둘 수 없습니다.")
    @Size(min = 4, max = 15, message = "ID는 최소 4자 이상, 최대 15자 이하입니다.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "ID는 알파벳 소문자(a~z)와 숫자(0~9)만 포함할 수 있습니다.")
    private String username;

    @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
    @Size(min = 4, max = 15, message = "비밀번호는 최소 4자 이상, 최대 15자 이하입니다.")
    private String password;

    @NotBlank(message = "이름은 비워둘 수 없습니다.")
    @Size(min = 2, max = 30, message = "이름은 최소 2자 이상, 최대 30자 이하입니다.")
    private String name;

    @NotBlank(message = "닉네임은 비워둘 수 없습니다.")
    @Size(min = 2, max = 15, message = "닉네임은 최소 2자 이상, 최대 15자 이하입니다.")
    private String nickname;

    @NotBlank(message = "이메일은(는) 비워둘 수 없습니다.")
    @Email(message = "유효한 이메일 주소 형식이어야 합니다.")
    private String email;

    @NotBlank(message = "Slack ID는 비워둘 수 없습니다.")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Slack ID는 알파벳, 숫자, 밑줄(_) 및 대시(-)만 포함할 수 있습니다.")
    private String slackId;

    @NotBlank(message = "전화번호는 비워둘 수 없습니다.")
    @Pattern(
            regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$",
            message = "전화번호는 000-0000-0000 형식이어야 합니다."
    )
    private String phoneNumber;

    @NotBlank(message = "역할은 필수 입력 항목입니다.")
    private UserRole role;
}
