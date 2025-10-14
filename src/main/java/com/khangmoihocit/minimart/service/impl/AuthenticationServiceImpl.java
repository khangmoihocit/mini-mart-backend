package com.khangmoihocit.minimart.service.impl;

import com.khangmoihocit.minimart.dto.UserDetailsCustom;
import com.khangmoihocit.minimart.dto.request.AuthenticationRequest;
import com.khangmoihocit.minimart.dto.response.AuthenticationResponse;
import com.khangmoihocit.minimart.entity.Token;
import com.khangmoihocit.minimart.entity.User;
import com.khangmoihocit.minimart.repository.TokenRepository;
import com.khangmoihocit.minimart.repository.UserRepository;
import com.khangmoihocit.minimart.service.AuthenticationService;
import com.khangmoihocit.minimart.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    JwtUtil jwtUtil;
    AuthenticationManager authenticationManager;
    UserDetailsServiceImpl userDetailsService;

    @Override
    @Transactional
    public AuthenticationResponse authentication(AuthenticationRequest request, HttpServletResponse httpResponse) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsCustom userDetails = (UserDetailsCustom) authentication.getPrincipal();

        String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true) //chỉ gửi cookie qua kết nối HTTPS
                .path("/api/v1/auth/refresh") //chỉ gửi cookie đến endpoint này
                .maxAge(7 * 24 * 60 * 60) // 7 days
                .sameSite("Strict") //chống tấn công CSRF
                .build();

        // Thêm cookie vào header của phản hồi HTTP
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    @Override
    public AuthenticationResponse refreshToken(String refreshToken) throws ParseException {
        final String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if(!jwtUtil.validateToken(refreshToken, userDetails.getUsername())) {
            return null;
        }

        String newAccessToken = jwtUtil.generateAccessToken(userDetails.getUsername());

        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }

    @Override
    public void logout(String authorizationHeader, String refreshToken, HttpServletResponse httpResponse)
            throws ParseException {
        //Xóa cookie ở phía client
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/auth/refresh")
                .maxAge(0) // Hết hạn ngay lập tức
                .sameSite("Strict")
                .build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        SecurityContextHolder.clearContext();
    }
}