package com.example.backend.service.impl;

import com.example.backend.dto.request.AuthenticationRequest;
import com.example.backend.dto.request.IntrospectRequest;
import com.example.backend.dto.request.RefreshRequest;
import com.example.backend.dto.response.AuthenticationResponse;
import com.example.backend.dto.response.IntrospectResponse;
import com.example.backend.entity.User;
import com.example.backend.exception.AppException;
import com.example.backend.exception.ErrorCode;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.IAuthenticationService;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
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

    @Override
    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException {
        var token = introspectRequest.getToken();
        boolean isValid = true;

        try {
            verifyToken(token);
        }catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    @Override
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

        String redisKey = user.getEmail();
        redisTemplate.opsForValue().set(redisKey, refreshToken);

        return AuthenticationResponse.builder().isAuthenticated(true)
                .accessToken(accessToken).refreshToken(refreshToken).build();
    }

    @Override
    public AuthenticationResponse refresh(RefreshRequest refreshRequest) throws JOSEException, ParseException {
        String refreshToken = refreshRequest.getRefreshToken();

        SignedJWT signedJWT = verifyToken(refreshToken);

        String userEmail = signedJWT.getJWTClaimsSet().getSubject();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String redisKey = user.getEmail();
        String storedToken = (String) redisTemplate.opsForValue().get(redisKey);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String accessToken = generateAccessToken(user);

        return AuthenticationResponse.builder()
                .isAuthenticated(true)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
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

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier jwsVerifier = new MACVerifier(secretKey.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        boolean isExpired = signedJWT.getJWTClaimsSet().getExpirationTime().before(new Date());
        boolean isValid = signedJWT.verify(jwsVerifier);

        if(isExpired || !isValid) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return signedJWT;
    }
}