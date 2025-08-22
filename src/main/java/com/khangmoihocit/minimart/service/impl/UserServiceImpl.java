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
import com.khangmoihocit.minimart.repository.UserRepository;
import com.khangmoihocit.minimart.service.UserService;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        Role roleUser = roleRepository.findByName("USER")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_USER_NOT_EXIST));

        user.setRole(roleUser);
        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateUser(UserUpdateInfoRequest request, String id) {
        if(Objects.isNull(id) || id.equals("")) throw new AppException(ErrorCode.ID_UPDATE_NOT_BLANK);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userMapper.updateUser(user, request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role roleUpdate = roleRepository.findByName(request.getRoleName())
                .orElseThrow(()-> new AppException(ErrorCode.ROLE_NOT_EXIST));
        user.setRole(roleUpdate);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getUser(String id) {
        return null;
    }

    @Override
    public UserResponse getMyInfo() {
        return null;
    }

    @Override
    public List<UserResponse> getUsers() {
        List<User> users = userRepository.findAll();
        if(users.isEmpty()) throw new AppException(ErrorCode.USER_NOT_FOUND);
        return users.stream().map(userMapper::toUserResponse).toList();
    }

    @Override
    public void deleteUser(String id) {
        if(!userRepository.existsById(id)) throw new AppException(ErrorCode.USER_NOT_EXIST);
        userRepository.deleteById(id);
    }
}
