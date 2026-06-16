package com.untripical.dto.userDto;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class UserUpdateResponseWithTokenDTO {
        private UserResponseDTO user;
        private String token;
}
