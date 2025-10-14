package com.khangmoihocit.minimart.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long ACCESS_TOKEN_VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESH_TOKEN_VALID_DURATION;

    public String generateAccessToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(Instant.now().plus(ACCESS_TOKEN_VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .signWith(SignatureAlgorithm.ES512, SIGNER_KEY)
                .compact();
    }

    public String getUserIdFromJWT(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(SIGNER_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(SIGNER_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenExpired(String token) {
        return Jwts.parser()
                .setSigningKey(SIGNER_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration().before(new Date());
    }

    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }
}
