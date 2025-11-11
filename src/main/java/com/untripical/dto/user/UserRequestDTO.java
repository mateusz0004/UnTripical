package com.untripical.dto.user;

import com.untripical.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class UserRequestDTO {

    @NotBlank(message = "email must not be blank")
    String email;

    @NotBlank(message = "username must not be blank")
    String username;

    @NotBlank(message = "password must not be blank")
    String password;

    @NotNull(message = "isActive must not be null")
    Boolean isActive;

    @NotNull(message = "userRole must not be null")
    UserRole userRole;


}
