package com.portfolio.stockanalyzer.service;

import com.portfolio.stockanalyzer.dto.request.LoginRequest;
import com.portfolio.stockanalyzer.dto.request.RegisterRequest;
import com.portfolio.stockanalyzer.dto.response.AuthResponse;
import com.portfolio.stockanalyzer.entity.User;
import com.portfolio.stockanalyzer.exception.DuplicateResourceException;
import com.portfolio.stockanalyzer.repository.UserRepository;
import com.portfolio.stockanalyzer.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already registered");
        }
        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .displayName(request.displayName())
                .build();
        userRepository.save(user);
        log.info("User registered: {}", request.email());
        String token = tokenProvider.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getDisplayName());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        var user = userRepository.findByEmail(request.email()).orElseThrow();
        String token = tokenProvider.generateToken(user.getEmail());
        log.info("User logged in: {}", request.email());
        return new AuthResponse(token, user.getEmail(), user.getDisplayName());
    }

    @Transactional(readOnly = true)
    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }
}
