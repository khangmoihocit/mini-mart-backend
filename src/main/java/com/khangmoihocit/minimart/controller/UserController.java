package com.khangmoihocit.minimart.controller;

import com.khangmoihocit.minimart.dto.request.UserCreationRequest;
import com.khangmoihocit.minimart.dto.request.UserUpdateInfoRequest;
import com.khangmoihocit.minimart.dto.response.ApiResponse;
import com.khangmoihocit.minimart.dto.response.UserResponse;
import com.khangmoihocit.minimart.entity.User;
import com.khangmoihocit.minimart.repository.UserRepository;
import com.khangmoihocit.minimart.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {
    UserService userService;

    @PostMapping
    ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    ApiResponse<UserResponse> updateUser(@Valid @RequestBody UserUpdateInfoRequest request,
                                         @PathVariable String id){
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(request, id))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    ApiResponse<List<UserResponse>> getAll(){
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteById(@PathVariable String id){
        userService.deleteUser(id);
        return ApiResponse.<Void>builder()
                .message("Xóa user thành công!")
                .build();
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

//    UserRepository userRepository;
//
//    @GetMapping("/test")
//    ResponseEntity<List<User>> gettest() {
//        return ResponseEntity.ok(userRepository.findUserActive());
//    }

    @GetMapping("/search")
    ApiResponse<Page<UserResponse>> searchUsers(@RequestParam(name="pageNo") int pageNo,
                                  @RequestParam(name="pageSize") int pageSize,
                                  @RequestParam(name="fullName", required = false) String fullName,
                                  @RequestParam(name="email", required = false) String email,
                                  @RequestParam(name="address", required = false) String address) {
        Page<UserResponse> users = userService.searchUser(fullName, email, address, pageNo, pageSize);
        return ApiResponse.<Page<UserResponse>>builder()
                .result(users)
                .build();
    }

}
