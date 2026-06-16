package com.untripical.dto.userDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class UserRegisterRequestDTO {
    @NotBlank(message = "Email must not be blank")
    private String email;
    @NotBlank(message = "Username must not be blank")
    private String username;
    @NotBlank(message = "Password must not be blank")
    private String password;
}
