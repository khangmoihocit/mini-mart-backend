package com.khangmoihocit.minimart.service;


import com.khangmoihocit.minimart.dto.request.PermissionRequest;
import com.khangmoihocit.minimart.dto.response.PermissionResponse;

import java.util.List;

public interface PermissionService {
    PermissionResponse create(PermissionRequest request);

    PermissionResponse get(String name);

    PermissionResponse update(PermissionRequest request);

    List<PermissionResponse> getAll();

    void delete(String name);
}
