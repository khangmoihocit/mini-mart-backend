package com.khangmoihocit.minimart.mapper;

import com.khangmoihocit.minimart.dto.response.PermissionResponse;
import com.khangmoihocit.minimart.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionResponse toPermissionResponse(Permission permission);
}
