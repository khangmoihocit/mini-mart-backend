package com.khangmoihocit.minimart.service;

import com.khangmoihocit.minimart.dto.request.AuthenticationRequest;
import com.khangmoihocit.minimart.dto.response.AuthenticationResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationResponse authentication(AuthenticationRequest request, HttpServletResponse response);

    void logout(String accessToken, String refreshToken, HttpServletResponse response) throws ParseException ;

    AuthenticationResponse refreshToken(String refreshToken, HttpServletResponse response) throws ParseException;
}