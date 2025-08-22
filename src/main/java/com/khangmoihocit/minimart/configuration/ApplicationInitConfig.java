package com.khangmoihocit.minimart.configuration;

import com.khangmoihocit.minimart.entity.Role;
import com.khangmoihocit.minimart.entity.User;
import com.khangmoihocit.minimart.enums.ErrorCode;
import com.khangmoihocit.minimart.exception.AppException;
import com.khangmoihocit.minimart.repository.RoleRepository;
import com.khangmoihocit.minimart.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
                if (!roleRepository.existsByName("USER")) {
                    roleRepository.save(Role.builder()
                            .name("USER")
                            .build());
                }

                if (!roleRepository.existsByName("ADMIN")) {
                    roleRepository.save(Role.builder()
                            .name("ADMIN")
                            .build());
                }

                var role = roleRepository.findByName("ADMIN")
                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_ADMIN_NOT_INITIALIZED));

                User user = User.builder()
                        .email("admin@gmail.com")
                        .password(passwordEncoder.encode("admin"))
                        .fullName("Phạm Văn Khang")
                        .phoneNumber("0987654321")
                        .role(role)
                        .build();
                userRepository.save(user);
            }
        };
    }
}



