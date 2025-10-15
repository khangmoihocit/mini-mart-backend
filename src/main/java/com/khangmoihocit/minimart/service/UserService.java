package com.khangmoihocit.minimart.service;

import com.khangmoihocit.minimart.dto.request.UserCreationRequest;
import com.khangmoihocit.minimart.dto.request.UserUpdateInfoRequest;
import com.khangmoihocit.minimart.dto.response.UserResponse;
import com.khangmoihocit.minimart.entity.User;
import org.springframework.data.domain.Page;


import java.util.List;

public interface UserService {
    UserResponse createUser(UserCreationRequest request);

    UserResponse updateUser(UserUpdateInfoRequest request, String id);

    UserResponse getUser(String id);

    UserResponse getMyInfo();

    List<UserResponse> getUsers();

    void deleteUser(String id);

    void activeAccount(String id);

    void unActiveAccount(String id);

    Page<User> search(int pageNo, int pageSize);

    Page<UserResponse> searchUser(int pageNo, int pageSize, String keyword);
}
