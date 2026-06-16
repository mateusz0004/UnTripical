package com.untripical.dto.userDto;

import lombok.Value;

@Value
public class LoginRequest {
    private String username;
    private String password;
}
