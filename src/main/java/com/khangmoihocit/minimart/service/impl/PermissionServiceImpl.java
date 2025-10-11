package com.khangmoihocit.minimart.service.impl;

import com.khangmoihocit.minimart.dto.request.PermissionRequest;
import com.khangmoihocit.minimart.dto.response.PermissionResponse;
import com.khangmoihocit.minimart.entity.Permission;
import com.khangmoihocit.minimart.enums.ErrorCode;
import com.khangmoihocit.minimart.exception.AppException;
import com.khangmoihocit.minimart.mapper.PermissionMapper;
import com.khangmoihocit.minimart.repository.PermissionRepository;
import com.khangmoihocit.minimart.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionServiceImpl implements PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    @Override
    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);

        return permissionMapper.toPermissionResponse(permission);
    }

    @Override
    public PermissionResponse get(String name) {
        return permissionMapper.toPermissionResponse(
                permissionRepository.findById(name)
                        .orElseThrow(()-> new AppException(ErrorCode.ROLE_NAME_NOT_FOUND)));
    }

    @Override
    public PermissionResponse update(PermissionRequest request) {
        Permission permission = Permission.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return permissionMapper.toPermissionResponse(permission);
    }

    @Override
    public List<PermissionResponse> getAll() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toPermissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String name) {
        Permission permission = permissionRepository.findById(name)
                .orElseThrow(()-> new AppException(ErrorCode.ROLE_NAME_NOT_FOUND));
        permissionRepository.deleteById(name);
    }
}
