package com.bobjool.presentation.controller;

import com.bobjool.application.service.UserService;
import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.PageResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.presentation.dto.request.UpdateUserReqDto;
import com.bobjool.presentation.dto.response.UpdateUserResDto;
import com.bobjool.presentation.dto.response.UserResDto;
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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResDto>> getUserById(@PathVariable Long id) {

        UserResDto response = userService.getUserById(id);

        return ApiResponse.success(
                SuccessCode.SUCCESS,
                response
        );
    }

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

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateUserResDto>> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserReqDto request
//            HttpServletRequest servletRequest
    ) {

        UpdateUserResDto response = userService.updateUser(request.toServiceDto(), id);

        return ApiResponse.success(
                SuccessCode.SUCCESS_UPDATE,
                response
        );
    }

    @PatchMapping("/{id}/approval")
    public ResponseEntity<ApiResponse<UserResDto>> updateUserApproval(
            @PathVariable Long id,
            @RequestBody Boolean approved
    ) {

        UserResDto response = userService.updateUserApproval(id, approved);

        return ApiResponse.success(
                SuccessCode.SUCCESS_UPDATE,
                response
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @PathVariable Long id
    ) {

        userService.deleteUser(id);

        return ApiResponse.success(
                SuccessCode.SUCCESS_DELETE
        );
    }
}
