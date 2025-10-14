package com.khangmoihocit.minimart.service.impl;

import com.khangmoihocit.minimart.dto.UserDetailsCustom;
import com.khangmoihocit.minimart.dto.request.AuthenticationRequest;
import com.khangmoihocit.minimart.dto.response.AuthenticationResponse;
import com.khangmoihocit.minimart.exception.OurException;
import com.khangmoihocit.minimart.repository.TokenRepository;
import com.khangmoihocit.minimart.repository.UserRepository;
import com.khangmoihocit.minimart.service.AuthenticationService;
import com.khangmoihocit.minimart.utils.JwtUtil;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    UserRepository userRepository;
    TokenRepository tokenRepository;
    JwtUtil jwtUtil;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthenticationResponse authentication(AuthenticationRequest request, HttpServletResponse httpResponse) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsCustom userDetails = (UserDetailsCustom) authentication.getPrincipal();
        if(userDetails == null || !userDetails.isEnabled()){
            throw new OurException("Bạn đã bị vô hiệu hóa tài khoản, vui lòng liên hệ quản trị viên.");
        }

        String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());



        return AuthenticationResponse.builder()
                .accessToken("")
                .build();
    }

    @Override
    @Transactional
    public AuthenticationResponse refreshToken(String refreshToken, HttpServletResponse httpResponse) throws ParseException, JOSEException {
        return null;
    }

    @Override
    public void logout(String accessTokenHeader, String refreshToken, HttpServletResponse httpResponse) throws ParseException, JOSEException {
//        //Vô hiệu hóa Access Token bằng Redis denylist
//        if (accessTokenHeader != null && accessTokenHeader.startsWith("Bearer ")) {
//            String accessToken = accessTokenHeader.substring(7);
//            jwtService.invalidateToken(accessToken);
//        }
//
//        //Vô hiệu hóa Refresh Token trong DB (logic cũ)
//        tokenRepository.findByToken(refreshToken)
//                .ifPresent(token -> {
//                    token.setRevoked(true);
//                    token.setExpired(true);
//                    tokenRepository.save(token);
//                });
//
//        //Xóa cookie ở phía client
//        Cookie cookie = new Cookie("refresh_token", null);
//        cookie.setMaxAge(0);
//        cookie.setHttpOnly(true);
//        cookie.setPath("/");
//        // cookie.setSecure(true); // Bật ở production
        httpResponse.addCookie(cookie);
    }

}