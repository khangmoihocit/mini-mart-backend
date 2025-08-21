package com.khangmoihocit.minimart.mapper;

import com.khangmoihocit.minimart.dto.request.UserCreationRequest;
import com.khangmoihocit.minimart.dto.response.UserResponse;
import com.khangmoihocit.minimart.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toUserResponse(User user);

    User toUser(UserCreationRequest request);

}
