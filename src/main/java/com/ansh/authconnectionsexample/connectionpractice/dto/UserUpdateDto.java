package com.ansh.authconnectionsexample.connectionpractice.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDto {
    private String experience;
    private Set<Long> skillIds;
}
