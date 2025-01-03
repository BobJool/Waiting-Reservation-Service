package com.bobjool.notification.presentation.controller;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.notification.application.service.TemplateService;
import com.bobjool.notification.presentation.request.TemplateReqDto;
import com.bobjool.notification.presentation.response.TemplateCreateResDto;
import com.bobjool.notification.presentation.response.TemplateDeleteResDto;
import com.bobjool.notification.presentation.response.TemplateSelectResDto;
import com.bobjool.notification.presentation.response.TemplateUpdateResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
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

    @PutMapping("/{templatesId}")
    public ResponseEntity<ApiResponse<TemplateUpdateResDto>> updateTemplate(
            @PathVariable UUID templatesId,
            @Valid @RequestBody TemplateReqDto reqDto) {
        TemplateUpdateResDto response = TemplateUpdateResDto.from(
                templateService.updateTemplate(templatesId, reqDto.toServiceDto())
        );

        return ApiResponse.success(SuccessCode.SUCCESS_UPDATE, response);
    }

    @DeleteMapping("/{templateId}")
    public ResponseEntity<ApiResponse<TemplateDeleteResDto>> deleteTemplate(@PathVariable UUID templateId) {
        TemplateDeleteResDto response = TemplateDeleteResDto.from(
                templateService.deleteTemplate(templateId)
        );
        return ApiResponse.success(SuccessCode.SUCCESS_DELETE, response);
    }

}