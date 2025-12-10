package com.ansh.authconnectionsexample.connectionpractice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Long id;
    private String username;
    private String email;
}
