package com.example.backend.service.impl;

import com.example.backend.dto.request.AuthenticationRequest;
import com.example.backend.dto.response.AuthenticationResponse;
import com.example.backend.entity.User;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.IAuthenticationService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationServiceImplement implements IAuthenticationService {
    UserRepository userRepository;
    RedisTemplate<String, Object> redisTemplate;
    PasswordEncoder passwordEncoder;
    @NonFinal
    @Value("${jwt.secret-key}")
    String secretKey;

    @NonFinal
    @Value("${jwt.access-token-expiration}")
    protected long accessTokenExpiration;

    @NonFinal
    @Value("${jwt.refresh-token-expiration}")
    protected long refreshTokenExpiration;

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        var user = userRepository
                .findByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        boolean isAuthenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());
        if(!isAuthenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);

        String redisKey = "refresh-token:" + user.getEmail();

        return null;
    }

    private String generateAccessToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("example.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(accessTokenExpiration, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(secretKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create refresh token", e);
            throw new RuntimeException(e);
        }
    }

    private String generateRefreshToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("example.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(refreshTokenExpiration, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(secretKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create refresh token", e);
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        return user.getRole().getName();
    }

}
