package com.khangmoihocit.minimart.mapper;

import com.khangmoihocit.minimart.dto.response.RoleResponse;
import com.khangmoihocit.minimart.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponse toRoleResponse(Role role);
}
