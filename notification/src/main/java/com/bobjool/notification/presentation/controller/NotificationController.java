package com.bobjool.notification.presentation.controller;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.PageResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.notification.application.service.NotificationService;
import com.bobjool.notification.presentation.request.NotificationReqDto;
import com.bobjool.notification.presentation.request.NotificationSearchReqDto;
import com.bobjool.notification.presentation.response.NotificationSearchResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping
    public void postNotification(@Valid @RequestBody NotificationReqDto reqDto) {
        notificationService.postNotification(reqDto.toServiceDto());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<NotificationSearchResDto>>> searchNotifications(
            @ModelAttribute NotificationSearchReqDto reqDto,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        Page<NotificationSearchResDto> response = notificationService.searchNotification(
                        NotificationSearchReqDto.toServiceDto(reqDto), pageable
                )
                .map(NotificationSearchResDto::from);

        return ApiResponse.success(SuccessCode.SUCCESS, PageResponse.of(response));
    }

}
