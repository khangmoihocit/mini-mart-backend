package com.khangmoihocit.minimart.service.impl;

import com.khangmoihocit.minimart.dto.request.UserCreationRequest;
import com.khangmoihocit.minimart.dto.request.UserUpdateInfoRequest;
import com.khangmoihocit.minimart.dto.response.UserResponse;
import com.khangmoihocit.minimart.entity.Role;
import com.khangmoihocit.minimart.entity.User;
import com.khangmoihocit.minimart.enums.ErrorCode;
import com.khangmoihocit.minimart.exception.AppException;
import com.khangmoihocit.minimart.mapper.UserMapper;
import com.khangmoihocit.minimart.repository.RoleRepository;
import com.khangmoihocit.minimart.repository.TokenRepository;
import com.khangmoihocit.minimart.repository.UserRepository;
import com.khangmoihocit.minimart.service.AuthenticationService;
import com.khangmoihocit.minimart.service.UserService;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    TokenRepository tokenRepository;
    AuthenticationService authenticationService;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.builder().name("USER").description("This is role user").build());

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateUser(UserUpdateInfoRequest request, String id) {
        if (Objects.isNull(id) || id.equals("")) throw new AppException(ErrorCode.ID_UPDATE_NOT_BLANK);

        User userUpdate = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userMapper.updateUser(userUpdate, request);

        //nếu update role -> mới cần query db
        if(!request.getRoleName().equals(userUpdate.getRole().getName())){
            Role role = roleRepository.findById(request.getRoleName())
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_USER_NOT_EXIST));
            userUpdate.setRole(role);
        }

        try {
            userUpdate = userRepository.save(userUpdate);

        } catch (DataIntegrityViolationException ex) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        return userMapper.toUserResponse(userUpdate);
    }

    @PostAuthorize("returnObject.username == authentication.name")
    @Override
    public UserResponse getUser(String id) {
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }

    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toUserResponse(user);
    }

    @Override
    public List<UserResponse> getUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) throw new AppException(ErrorCode.USER_NOT_FOUND);
        return users.stream().map(userMapper::toUserResponse).toList();
    }

    @Override
    public void deleteUser(String id) {
        if(!userRepository.existsById(id)) throw new AppException(ErrorCode.USER_NOT_EXIST);
        userRepository.deleteById(id);
    }

    @Override
    public void activeAccount(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXIST));

        user.setIsActive(true);
        userRepository.save(user);
    }

    @Override
    public void unActiveAccount(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXIST));
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public Page<User> search(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo-1, pageSize);
        return userRepository.findAll(pageable);
    }

    @Override
    public Page<UserResponse> searchUser(String fullName, String email, String address, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo-1, pageSize);
        Page<User> users = userRepository.search(fullName, email, address, pageable);
        if (!users.isEmpty()) {
            return users.map(userMapper::toUserResponse);
        }
        return null;
    }

}
