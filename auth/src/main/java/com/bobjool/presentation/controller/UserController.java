package com.bobjool.presentation.controller;

import com.bobjool.application.dto.UserResDto;
import com.bobjool.application.service.UserService;
import com.bobjool.common.infra.aspect.RequireRole;
import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.PageResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.presentation.dto.request.UpdateUserReqDto;
import com.bobjool.application.dto.UpdateUserResDto;
import com.bobjool.application.dto.UserContactResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @RequireRole(value = {"CUSTOMER", "OWNER", "MASTER"})
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResDto>> getUserById(@PathVariable Long id) {

        UserResDto response = userService.getUserById(id);

        return ApiResponse.success(
                SuccessCode.SUCCESS,
                response
        );
    }

    @RequireRole(value = {"MASTER"})
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResDto>>> search(
            @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable){

        Page<UserResDto> responsePage
                = userService.search(pageable);

        return ApiResponse.success(
                SuccessCode.SUCCESS,
                PageResponse.of(responsePage)
        );
    }

    @RequireRole(value = {"CUSTOMER", "OWNER", "MASTER"})
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateUserResDto>> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserReqDto request
    ) {

        UpdateUserResDto response = userService.updateUser(request.toServiceDto(), id);

        return ApiResponse.success(
                SuccessCode.SUCCESS_UPDATE,
                response
        );
    }

    @RequireRole(value = {"CUSTOMER", "OWNER", "MASTER"})
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @PathVariable Long id
    ) {

        userService.deleteUser(id);

        return ApiResponse.success(
                SuccessCode.SUCCESS_DELETE
        );
    }

    @RequireRole(value = {"OWNER", "MASTER"})
    @GetMapping("/{id}/contact")
    public ResponseEntity<ApiResponse<UserContactResDto>> getContact(
            @PathVariable Long id
    ) {

        UserContactResDto response = userService.getContact(id);

        return ApiResponse.success(
                SuccessCode.SUCCESS,
                response
        );
    }
}
