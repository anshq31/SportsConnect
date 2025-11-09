package com.ansh.authconnectionsexample.connectionpractice.service;

import com.ansh.authconnectionsexample.connectionpractice.dto.AuthRequest;
import com.ansh.authconnectionsexample.connectionpractice.dto.AuthResponse;
import com.ansh.authconnectionsexample.connectionpractice.dto.LoginRequest;
import com.ansh.authconnectionsexample.connectionpractice.dto.RefreshRequest;
import com.ansh.authconnectionsexample.connectionpractice.model.userAndAuthEntities.RefreshToken;
import com.ansh.authconnectionsexample.connectionpractice.model.userAndAuthEntities.User;
import com.ansh.authconnectionsexample.connectionpractice.repository.UserRepository;
import com.ansh.authconnectionsexample.connectionpractice.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(AuthRequest authRequest){
        if (userRepository.existsByUsername(authRequest.getUsername())){
            throw new RuntimeException("User already exist");
        }

        if(userRepository.existsByEmail(authRequest.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(authRequest.getUsername())
                .email(authRequest.getEmail())
                .password(passwordEncoder.encode(authRequest.getPassword()))
                .build();
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public AuthResponse login(LoginRequest authRequest){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );

        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(()-> new RuntimeException("User not found"));

        String accessToken = jwtService.generateAccessToken(user.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public AuthResponse refreshToken(RefreshRequest refreshRequest){
        String requestToken = refreshRequest.getRefreshToken();
        Optional<RefreshToken> optional = refreshTokenService.findByToken(requestToken);
        if (optional.isEmpty()){
            throw  new RuntimeException("Invalid refresh token");
        }

        RefreshToken savedToken = optional.get();

        if (refreshTokenService.isExpired(savedToken)){
            refreshTokenService.deleteByUser(savedToken.getUser());
            throw new RuntimeException("refresh token expired. Please login again ");
        }

        String newAccessToken = jwtService.generateAccessToken(savedToken.getUser().getUsername());

        refreshTokenService.deleteByUser(savedToken.getUser());
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(savedToken.getUser());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .build();
    }

    public void logout(String username){
        userRepository.findByUsername(username).ifPresent(refreshTokenService::deleteByUser);
    }
}
