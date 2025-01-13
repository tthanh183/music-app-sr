package com.example.backend.service;

import com.example.backend.dto.request.AuthenticationRequest;
import com.example.backend.dto.response.AuthenticationResponse;

public interface IAuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);
}
