package com.khangmoihocit.minimart.service.impl;

import com.khangmoihocit.minimart.entity.User; // Đảm bảo bạn import đúng entity User của mình
import com.khangmoihocit.minimart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.User; // Import này có thể gây nhầm lẫn, hãy dùng tên đầy đủ
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getIsActive(),
                true, // accountNonExpired: tài khoản không hết hạn
                true, // credentialsNonExpired: chứng thực không hết hạn
                true, // accountNonLocked: tài khoản không bị khóa
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().getName()))
        );
    }
}