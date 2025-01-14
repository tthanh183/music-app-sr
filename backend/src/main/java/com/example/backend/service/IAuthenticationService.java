package com.example.backend.service;

import com.example.backend.dto.request.*;
import com.example.backend.dto.response.AuthenticationResponse;
import com.example.backend.dto.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface IAuthenticationService {
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);
    public AuthenticationResponse refresh(RefreshRequest refreshRequest) throws JOSEException, ParseException;
    public void logout(LogoutRequest logoutRequest) throws ParseException, JOSEException;
    public void signup(SignupRequest signupRequest);
}
