package com.khangmoihocit.minimart.service;

import com.khangmoihocit.minimart.dto.request.AuthenticationRequest;
import com.khangmoihocit.minimart.dto.request.IntrospectRequest;
import com.khangmoihocit.minimart.dto.request.LogoutRequest;
import com.khangmoihocit.minimart.dto.response.AuthenticationResponse;
import com.khangmoihocit.minimart.dto.response.IntrospectResponse;

public interface AuthenticationService {
    IntrospectResponse introspect(IntrospectRequest request);

    AuthenticationResponse authentication(AuthenticationRequest request);

//    void logout(LogoutRequest request);


}
