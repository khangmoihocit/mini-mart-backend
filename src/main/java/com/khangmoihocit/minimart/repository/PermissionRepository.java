package com.khangmoihocit.minimart.repository;

import com.khangmoihocit.minimart.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, String> {
}
