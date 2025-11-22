package com.untripical.controller;

import com.untripical.dto.userDto.*;
import com.untripical.model.User;
import com.untripical.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/user")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public UserResponseDTO UserResponseDTO() {
        return userService.getCurrentUserResponseDTO();
    }

    @PostMapping("/register")
    public User register(@RequestBody UserRegisterRequestDTO user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest login) {
        return userService.verify(login);
    }

    @DeleteMapping
    public void delete() {
        userService.deleteUser();
    }

    @PutMapping("/update")
    public UserUpdateResponseWithTokenDTO updateUser(@RequestBody UserUpdateDTO dto) {
        return userService.updateUser(dto);
    }

    @PutMapping("/admin")
    public UserResponseDTO setAdmins() {
        return userService.setAdmins();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUserOrGuideByAdmin(@PathVariable Long id) {
        userService.deleteUserByAdmin(id);
    }
}

