package com.khangmoihocit.minimart.service.impl;

import com.khangmoihocit.minimart.dto.request.UserCreationRequest;
import com.khangmoihocit.minimart.dto.response.UserResponse;
import com.khangmoihocit.minimart.entity.Role;
import com.khangmoihocit.minimart.entity.User;
import com.khangmoihocit.minimart.mapper.UserMapper;
import com.khangmoihocit.minimart.repository.RoleRepository;
import com.khangmoihocit.minimart.repository.UserRepository;
import com.khangmoihocit.minimart.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if(!roleRepository.existsByName("USER")){
            Role roleUser = Role.builder().name("USER").build();
            roleRepository.save(roleUser);
        }else{
            Role role = roleRepository.findByName("USER");
            user.setRole(role);
        }

        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }
}
