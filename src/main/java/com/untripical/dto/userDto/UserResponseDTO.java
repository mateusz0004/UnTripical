package com.untripical.dto.userDto;

import com.untripical.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
     String email;
     String username;
     UserRole userRole;
}
