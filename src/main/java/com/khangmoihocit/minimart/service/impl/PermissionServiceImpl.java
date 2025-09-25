package com.khangmoihocit.minimart.service.impl;

import com.khangmoihocit.minimart.dto.request.PermissionRequest;
import com.khangmoihocit.minimart.dto.response.PermissionResponse;
import com.khangmoihocit.minimart.entity.Permission;
import com.khangmoihocit.minimart.mapper.PermissionMapper;
import com.khangmoihocit.minimart.repository.PermissionRepository;
import com.khangmoihocit.minimart.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return null;
    }

    @Override
    public PermissionResponse update(PermissionRequest request) {
        return null;
    }

    @Override
    public List<PermissionResponse> getAll() {
        return List.of();
    }

    @Override
    public void delete(String name) {

    }
}
