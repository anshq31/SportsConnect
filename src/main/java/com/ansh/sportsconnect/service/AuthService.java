package com.ansh.sportsconnect.service;

import com.ansh.sportsconnect.dto.AuthRequest;
import com.ansh.sportsconnect.dto.AuthResponse;
import com.ansh.sportsconnect.dto.LoginRequest;
import com.ansh.sportsconnect.dto.RefreshRequest;
import com.ansh.sportsconnect.model.userAndAuthEntities.RefreshToken;
import com.ansh.sportsconnect.model.userAndAuthEntities.User;
import com.ansh.sportsconnect.repository.RefreshTokenRepository;
import com.ansh.sportsconnect.repository.UserRepository;
import com.ansh.sportsconnect.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

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
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
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
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(RefreshRequest refreshRequest){
        String requestToken = refreshRequest.getRefreshToken();
        Optional<RefreshToken> optional = refreshTokenService.findByToken(requestToken);
        if (optional.isEmpty()){
            throw  new RuntimeException("Invalid refresh token");
        }

        RefreshToken savedToken = optional.get();

        if (refreshTokenService.isExpired(savedToken)){
            refreshTokenRepository.delete(savedToken);
            throw new RuntimeException("Refresh token expired. Please login again ");
        }

        User user = savedToken.getUser();
        String username = user.getUsername();
        String email = user.getEmail();
        Long id = user.getId();

        String newAccessToken = jwtService.generateAccessToken(savedToken.getUser().getUsername());

        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(savedToken.getUser());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .id(id)
                .username(username)
                .email(email)
                .build();
    }
}
