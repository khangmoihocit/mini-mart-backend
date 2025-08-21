package com.khangmoihocit.minimart.repository;

import com.khangmoihocit.minimart.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
    boolean existsByName(String name);
    Role findByName(String name);
}
