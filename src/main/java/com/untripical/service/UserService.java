package com.untripical.service;

import com.untripical.dto.userDto.LoginRequest;
import com.untripical.dto.userDto.RegisterRequest;
import com.untripical.enums.UserRole;
import com.untripical.exception.user.IncorrectRoleTypeException;
import com.untripical.exception.user.UserDoesNotExist;
import com.untripical.exception.user.UserWithThisUsernameAlreadyExist;
import com.untripical.model.User;
import com.untripical.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private PasswordEncoder encoder;

    public User register (RegisterRequest dto){
        User user = new User();
        user.setEmail(dto.getEmail());
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new UserWithThisUsernameAlreadyExist("This username already exist");
        }
        if(dto.getRole().toString().equals("ADMIN")){
            throw new IncorrectRoleTypeException("Incorrect role type");
        }
        user.setUsername(dto.getUsername());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setUserRole(dto.getRole());
        user.setIsActive(true);
        return userRepository.save(user);
    }
    public String verify (LoginRequest user){
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if(authentication.isAuthenticated()) return jwtService.generateToken(user.getUsername());
        return "fail";
    }
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("This username doesn't exist"));
        return user;
    }
    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }
    public User setAdmins(Long id){
        User admin = userRepository.findById(id).
                orElseThrow(() -> new UserDoesNotExist("This user does not exist"));
        if(admin.getUsername().equals("arek")||admin.getUsername().equals("mateusz")){
            admin.setUserRole(UserRole.ADMIN);
        }
        return userRepository.save(admin);
    }
}
