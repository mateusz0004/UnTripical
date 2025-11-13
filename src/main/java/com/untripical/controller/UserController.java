package com.untripical.controller;
import com.untripical.dto.userDto.LoginRequest;
import com.untripical.dto.userDto.RegisterRequest;
import com.untripical.model.User;
import com.untripical.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest user){
        return userService.register(user);
    }
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest login){
        return userService.verify(login);
    }
}
