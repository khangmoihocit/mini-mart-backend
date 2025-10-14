package com.khangmoihocit.minimart.service.impl;

import com.khangmoihocit.minimart.dto.UserDetailsCustom;
import com.khangmoihocit.minimart.entity.User;
import com.khangmoihocit.minimart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override //trả về userdetails hoặc lớp con kế thừa userdetails (UserDetailsCustom đã tạo)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(username);
        if(userOptional.isPresent()){
            UserDetailsCustom userDetailsCustom = UserDetailsCustom.builder() //user sẽ gán vào spring security
                    .id(userOptional.get().getId())
                    .username(userOptional.get().getEmail())
                    .password(userOptional.get().getPassword())
                    .isActive(userOptional.get().getIsActive())
                    .authorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_" + userOptional.get().getRole().getName())))
                    .build();
            return userDetailsCustom;
        }else{
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
    }
}
