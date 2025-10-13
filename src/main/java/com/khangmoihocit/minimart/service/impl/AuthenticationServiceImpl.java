package com.khangmoihocit.minimart.service.impl;

import com.khangmoihocit.minimart.dto.request.AuthenticationRequest;
import com.khangmoihocit.minimart.dto.response.AuthenticationResponse;
import com.khangmoihocit.minimart.entity.Token;
import com.khangmoihocit.minimart.entity.User;
import com.khangmoihocit.minimart.enums.ErrorCode;
import com.khangmoihocit.minimart.exception.AppException;
import com.khangmoihocit.minimart.repository.TokenRepository;
import com.khangmoihocit.minimart.repository.UserRepository;
import com.khangmoihocit.minimart.service.AuthenticationService;
import com.khangmoihocit.minimart.service.JwtService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    JwtService jwtService;
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthenticationResponse authentication(AuthenticationRequest request, HttpServletResponse httpResponse) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.LOGIN_FAILED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.LOGIN_FAILED);
        }

        if (!user.getIsActive()) {
            throw new AppException(ErrorCode.USER_NOT_ACTIVE);
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Thu hồi tất cả token cũ của user và lưu refresh token mới
        revokeAllUserTokens(user);
        saveRefreshTokenToDb(user, refreshToken);

        addRefreshTokenToCookie(refreshToken, httpResponse);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    @Override
    @Transactional
    public AuthenticationResponse refreshToken(String refreshToken, HttpServletResponse httpResponse) throws ParseException, JOSEException {
        Token tokenInDb = tokenRepository.findByToken(refreshToken)
                .filter(t -> !t.getRevoked() && !t.getExpired())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        // Xác thực chữ ký và hạn dùng của refresh token
        jwtService.verifyAndParseToken(refreshToken);

        User user = tokenInDb.getUser();
        String newAccessToken = jwtService.generateAccessToken(user);

        // Refresh Token Rotation
        String newRefreshToken = jwtService.generateRefreshToken(user);
        tokenInDb.setRevoked(true);
        tokenInDb.setExpired(true);
        tokenRepository.save(tokenInDb);

        saveRefreshTokenToDb(user, newRefreshToken);
        addRefreshTokenToCookie(newRefreshToken, httpResponse);

        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }

    @Override
    public void logout(String accessTokenHeader, String refreshToken, HttpServletResponse httpResponse) throws ParseException, JOSEException {
        //Vô hiệu hóa Access Token bằng Redis denylist
        if (accessTokenHeader != null && accessTokenHeader.startsWith("Bearer ")) {
            String accessToken = accessTokenHeader.substring(7);
            jwtService.invalidateToken(accessToken);
        }

        //Vô hiệu hóa Refresh Token trong DB (logic cũ)
        tokenRepository.findByToken(refreshToken)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    token.setExpired(true);
                    tokenRepository.save(token);
                });

        //Xóa cookie ở phía client
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        // cookie.setSecure(true); // Bật ở production
        httpResponse.addCookie(cookie);
    }

    private void saveRefreshTokenToDb(User user, String token) {
        Token tokenEntity = Token.builder()
                .token(token)
                .user(user)
                .tokenType("BEARER_REFRESH")
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(tokenEntity);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void addRefreshTokenToCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        // cookie.setSecure(true); // Dùng cờ này khi deploy trên HTTPS (production)
        response.addCookie(cookie);
    }
}