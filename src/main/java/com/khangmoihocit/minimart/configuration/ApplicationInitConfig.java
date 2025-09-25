//package com.khangmoihocit.minimart.configuration;
//
//import com.khangmoihocit.minimart.entity.Permission;
//import com.khangmoihocit.minimart.entity.Role;
//import com.khangmoihocit.minimart.entity.User;
//import com.khangmoihocit.minimart.enums.ErrorCode;
//import com.khangmoihocit.minimart.exception.AppException;
//import com.khangmoihocit.minimart.repository.PermissionService;
//import com.khangmoihocit.minimart.repository.RoleRepository;
//import com.khangmoihocit.minimart.repository.UserRepository;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.*;
//
//
//@Configuration
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class ApplicationInitConfig {
//    PasswordEncoder passwordEncoder;
//
//    @Bean
//    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository, PermissionService permissionRepository) {
//        return args -> {
//            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
//                if (!roleRepository.existsById("USER")) {
//                    roleRepository.save(Role.builder()
//                            .name("USER")
//                            .description("This is role user")
//                            .build());
//                }
//
//                if (!roleRepository.existsById("ADMIN")) {
//                    roleRepository.save(Role.builder()
//                            .name("ADMIN").description("This is role admin")
//                            .build());
//                }
//
//                if (userRepository.existsByEmail("admin@gmail.com")) {
//                    return;
//                }
//
//                Role role = roleRepository.findById("ADMIN")
//                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_ADMIN_NOT_INITIALIZED));
//
//                Permission permission = Permission.builder()
//                        .name("UPDATE_DATA")
//                        .build();
//                Permission permission2 = Permission.builder()
//                        .name("DELETE_DATA")
//                        .build();
//                Permission permission1 = Permission.builder()
//                        .name("CREATE_DATA")
//                        .build();
//
//                permissionRepository.save(permission1);
//                permissionRepository.save(permission);
//                permissionRepository.save(permission2);
//
//                Set<Permission> permissions = new HashSet<>();
//                permissions.add(permission);
//                permissions.add(permission1);
//                permissions.add(permission2);
//
//                role.setPermissions(permissions);
//
//                Set<Role> roles = new HashSet<>();
//                roles.add(role);
//
//                User user = User.builder()
//                        .email("admin@gmail.com")
//                        .password(passwordEncoder.encode("admin1802"))
//                        .fullName("Phạm Văn Khang")
//                        .phoneNumber("0987654321")
//                        .roles(new HashSet<>(roles))
//                        .build();
//
//                userRepository.save(user);
//            }
//        };
//    }
//}