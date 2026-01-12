package com.kt.login.dto;

import lombok.*;

@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class UserRegistrationDto {
    private String username;
    private String email;
    private String password;
}
