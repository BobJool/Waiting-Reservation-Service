package com.bobjool.notification.presentation.controller;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.notification.application.service.TemplateService;
import com.bobjool.notification.presentation.request.TemplateReqDto;
import com.bobjool.notification.presentation.response.TemplateCreateResDto;
import com.bobjool.notification.presentation.response.TemplateSelectResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notifications/templates")
@RequiredArgsConstructor
public class TemplateController {
    private final TemplateService templateService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TemplateSelectResDto>>> getTemplates() {
        List<TemplateSelectResDto> response = templateService.selectTemplate().stream()
                .map(TemplateSelectResDto::from)
                .collect(Collectors.toList());
        return ApiResponse.success(SuccessCode.SUCCESS, response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TemplateCreateResDto>> createTemplate(
            @Valid @RequestBody TemplateReqDto reqDto) {
        TemplateCreateResDto response = TemplateCreateResDto.from(
                templateService.createTemplate(reqDto.toServiceDto())
        );

        return ApiResponse.success(SuccessCode.SUCCESS_INSERT, response);
    }

}