package com.khangmoihocit.minimart.service;

import com.khangmoihocit.minimart.entity.User;
import com.khangmoihocit.minimart.exception.AppException;
import com.khangmoihocit.minimart.enums.ErrorCode;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtService {
    RedisTemplate<String, Object> redisTemplate;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long ACCESS_TOKEN_VALID_DURATION; // Thời gian hiệu lực cho Access Token (giây)

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESH_TOKEN_VALID_DURATION; // Thời gian hiệu lực cho Refresh Token (giây)

    public String generateAccessToken(User user) {
        return generateToken(user.getEmail(), buildScope(user), ACCESS_TOKEN_VALID_DURATION);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user.getEmail(), null, REFRESH_TOKEN_VALID_DURATION);
    }

    public SignedJWT verifyAndParseToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        if (!signedJWT.verify(verifier)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (signedJWT.getJWTClaimsSet().getExpirationTime().before(new Date())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Kiểm tra token có trong denylist (danh sách đen) không
        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        if (Boolean.TRUE.equals(redisTemplate.hasKey("denylist:" + jti))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    public void invalidateToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = verifyAndParseToken(token);
        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        long remainingTime = expirationTime.getTime() - System.currentTimeMillis();

        // Thêm JTI của token vào denylist trong Redis với thời gian hết hạn còn lại
        redisTemplate.opsForValue().set("denylist:" + jti, true, remainingTime, TimeUnit.MILLISECONDS);
    }


    private String generateToken(String username, String scope, long duration) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("khangmoihocit.com")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(duration, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString());

        if (scope != null && !scope.isEmpty()) {
            claimsBuilder.claim("scope", scope);
        }

        Payload payload = new Payload(claimsBuilder.build().toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        stringJoiner.add("ROLE_" + user.getRole().getName());
        return stringJoiner.toString();
    }
}