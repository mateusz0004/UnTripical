package com.untripical.mapper.user;

import com.untripical.dto.userDto.UserResponseDTO;
import com.untripical.dto.userDto.UserUpdateDTO;
import com.untripical.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toResponse(User entity);
    UserUpdateDTO toUpdate(User entity);
}
