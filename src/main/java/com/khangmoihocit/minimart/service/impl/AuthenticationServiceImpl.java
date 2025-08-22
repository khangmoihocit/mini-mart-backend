package com.khangmoihocit.minimart.service.impl;

import com.khangmoihocit.minimart.dto.request.AuthenticationRequest;
import com.khangmoihocit.minimart.dto.request.IntrospectRequest;
import com.khangmoihocit.minimart.dto.response.AuthenticationResponse;
import com.khangmoihocit.minimart.dto.response.IntrospectResponse;
import com.khangmoihocit.minimart.entity.Token;
import com.khangmoihocit.minimart.entity.User;
import com.khangmoihocit.minimart.enums.ErrorCode;
import com.khangmoihocit.minimart.exception.AppException;
import com.khangmoihocit.minimart.repository.RoleRepository;
import com.khangmoihocit.minimart.repository.TokenRepository;
import com.khangmoihocit.minimart.repository.UserRepository;
import com.khangmoihocit.minimart.service.AuthenticationService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    UserRepository userRepository;
    TokenRepository tokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) {
        boolean isValid = true;
        try {
            SignedJWT signedJWT = verifyToken(request.getToken());
        } catch (Exception e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    @Override
    public AuthenticationResponse authentication(AuthenticationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.LOGIN_FAILED);
        }

        String tokenString = generateToken(user);

        saveToken(user, tokenString);

        return AuthenticationResponse.builder()
                .token(tokenString)
                .authenticated(true)
                .build();
    }

    private void saveToken(User user, String tokenString) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(tokenString);
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            Token token = Token.builder()
                    .token(tokenString)
                    .user(user)
                    .tokenType("BEARER")
                    .expirationTime(expirationTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .revoked(false)
                    .expired(false)
                    .build();

            tokenRepository.save(token);
        } catch (ParseException e) {
            log.error("Cannot parse token to save: {}", e.getMessage());
        }
    }

    private SignedJWT verifyToken(String tokenString) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(tokenString);

        // 1. Xác thực chữ ký của token
        boolean verifiedSignature = signedJWT.verify(verifier);
        if (!verifiedSignature) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // 2. Kiểm tra token có trong database và còn hợp lệ không
        var tokenInDb = tokenRepository.findByToken(tokenString)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        if (tokenInDb.getRevoked() || tokenInDb.getExpired()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // 3. Kiểm tra token đã hết hạn chưa
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expirationTime.before(new Date())) {
            // Nếu hết hạn, cập nhật trạng thái trong DB và báo lỗi
            tokenInDb.setExpired(true);
            tokenRepository.save(tokenInDb);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("khangmoihocit.com")
                .issueTime(new Date())
                .expirationTime(new Date
                        (Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    //custom SCOPE_ sang ROLE_
    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if(user.getRole() != null){
            stringJoiner.add("ROLE_" + user.getRole().getName());
        }
        return stringJoiner.toString();
    }
}
