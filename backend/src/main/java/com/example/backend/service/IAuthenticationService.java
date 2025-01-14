package com.example.backend.service;

import com.example.backend.dto.request.AuthenticationRequest;
import com.example.backend.dto.request.IntrospectRequest;
import com.example.backend.dto.request.RefreshRequest;
import com.example.backend.dto.response.AuthenticationResponse;
import com.example.backend.dto.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface IAuthenticationService {
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);
    public AuthenticationResponse refresh(RefreshRequest refreshRequest) throws JOSEException, ParseException;
}
