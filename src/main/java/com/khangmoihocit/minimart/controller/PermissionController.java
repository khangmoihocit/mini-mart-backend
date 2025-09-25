package com.khangmoihocit.minimart.controller;

import com.khangmoihocit.minimart.dto.request.PermissionRequest;
import com.khangmoihocit.minimart.dto.response.ApiResponse;
import com.khangmoihocit.minimart.dto.response.PermissionResponse;
import com.khangmoihocit.minimart.service.PermissionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    @PreAuthorize("hasAuthority('UPDATE_DATA')")
    ApiResponse<PermissionResponse> addNew(@Valid @RequestBody PermissionRequest request){
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.create(request))
                .build();
    }
}
