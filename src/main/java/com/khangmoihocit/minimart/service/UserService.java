package com.khangmoihocit.minimart.service;

import com.khangmoihocit.minimart.dto.request.UserCreationRequest;
import com.khangmoihocit.minimart.dto.request.UserUpdateInfoRequest;
import com.khangmoihocit.minimart.dto.response.UserResponse;

public interface UserService {
    UserResponse createUser(UserCreationRequest request);
}
