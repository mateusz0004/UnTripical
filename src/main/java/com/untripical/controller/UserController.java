package com.untripical.controller;
import com.untripical.dto.userDto.LoginRequest;
import com.untripical.dto.userDto.RegisterRequest;
import com.untripical.model.User;
import com.untripical.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        userService.deleteUser(id);
    }
    @PutMapping("/{id}")
    public User setAdmins(@PathVariable Long id){
        return userService.setAdmins(id);
    }
}
/// //////// naprawić, żeby logowało bez RequestBody tylko przez BasicAuth
