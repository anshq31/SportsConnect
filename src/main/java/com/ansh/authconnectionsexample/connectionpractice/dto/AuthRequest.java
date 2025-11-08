package com.ansh.authconnectionsexample.connectionpractice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequest {
    private String username;
    private String email;
    private String password;
}
