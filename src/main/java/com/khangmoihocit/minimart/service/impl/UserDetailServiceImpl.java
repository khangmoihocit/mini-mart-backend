package com.khangmoihocit.minimart.service.impl;

import com.khangmoihocit.minimart.dto.UserSecurity;
import com.khangmoihocit.minimart.entity.User;
import com.khangmoihocit.minimart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override //trả về userdetails hoặc lớp con kế thừa userdetails (UserSecurity đã tạo)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(username);
        if(userOptional.isPresent()){
            UserSecurity userSecurity = UserSecurity.builder()
                    .username(userOptional.get().getEmail())
                    .password(userOptional.get().getPassword())
                    .build();
            return userSecurity;
        }else{
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
    }
}
