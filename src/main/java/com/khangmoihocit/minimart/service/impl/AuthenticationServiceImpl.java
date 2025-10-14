package com.khangmoihocit.minimart.service.impl;

import com.khangmoihocit.minimart.dto.UserDetailsCustom;
import com.khangmoihocit.minimart.dto.request.AuthenticationRequest;
import com.khangmoihocit.minimart.dto.response.AuthenticationResponse;
import com.khangmoihocit.minimart.exception.OurException;
import com.khangmoihocit.minimart.repository.TokenRepository;
import com.khangmoihocit.minimart.repository.UserRepository;
import com.khangmoihocit.minimart.service.AuthenticationService;
import com.khangmoihocit.minimart.utils.JwtUtil;
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

        String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    @Override
    @Transactional
    public AuthenticationResponse refreshToken(String refreshToken, HttpServletResponse httpResponse) throws ParseException {
        return null;
    }

    @Override
    public void logout(String accessTokenHeader, String refreshToken, HttpServletResponse httpResponse) throws ParseException {
    }

}