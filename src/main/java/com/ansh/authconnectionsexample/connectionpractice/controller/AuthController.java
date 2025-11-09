package com.ansh.authconnectionsexample.connectionpractice.controller;

import com.ansh.authconnectionsexample.connectionpractice.dto.AuthRequest;
import com.ansh.authconnectionsexample.connectionpractice.dto.AuthResponse;
import com.ansh.authconnectionsexample.connectionpractice.dto.LoginRequest;
import com.ansh.authconnectionsexample.connectionpractice.dto.RefreshRequest;
import com.ansh.authconnectionsexample.connectionpractice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request){

        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request){
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String username){
        authService.logout(username);
        return ResponseEntity.ok().build();
    }
}
