package com.bobjool.presentation.controller;

import com.bobjool.application.service.UserService;
import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.presentation.dto.response.UserResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResDto>> getUserById(@PathVariable Long id) {

        UserResDto response = userService.getUserById(id);

        return ApiResponse.success(
                SuccessCode.SUCCESS,
                response
        );
    }
}
