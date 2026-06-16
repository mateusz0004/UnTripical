package com.untripical.service;

import com.untripical.dto.userDto.*;
import com.untripical.enums.UserRole;
import com.untripical.exception.user.IncorrectRoleTypeException;
import com.untripical.exception.user.UserDoesNotExist;
import com.untripical.exception.user.UserWithThisUsernameAlreadyExist;
import com.untripical.mapper.user.UserMapper;
import com.untripical.model.User;
import com.untripical.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private HttpServletRequest request;

    public User register(UserRegisterRequestDTO dto) {
        if (request.getHeader("Authorization") != null) {
            throw new RuntimeException("You are already logged in");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new UserWithThisUsernameAlreadyExist("This username already exist");
        }
        user.setUsername(dto.getUsername());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setUserRole(UserRole.USER);
        return userRepository.save(user);
    }

    public User registerForGuides(UserRegisterRequestDTO dto, UserRole role) {
        if (request.getHeader("Authorization") != null) {
            throw new RuntimeException("You are already logged in");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new UserWithThisUsernameAlreadyExist("This username already exist");
        }
        if (role.toString().equals("ADMIN") || role.equals("USER")) {
            throw new IncorrectRoleTypeException("Incorrect role type");
        }
        user.setUsername(dto.getUsername());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setUserRole(role);
        return userRepository.save(user);
    }

    public UserUpdateResponseWithTokenDTO updateUser(UserUpdateDTO dto) {
        User user = getCurrentUser();
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }
        if (dto.getPassword() != null) {
            user.setPassword(encoder.encode(dto.getPassword()));
        }
        userRepository.save(user);
        SecurityContextHolder.clearContext();
        String newToken = jwtService.generateToken(user.getUsername());

         return UserUpdateResponseWithTokenDTO.builder()
                .user(userMapper.toResponse(user))
                .token(newToken)
                .build();
    }

    public String verify(LoginRequest user) {
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        User currentUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new UserDoesNotExist("This user does not exist"));

        if (!currentUser.getIsActive()) {
            throw new UserDoesNotExist("This account is deactivated");
        }

        if (authentication.isAuthenticated()){
            return jwtService.generateToken(user.getUsername());
        }
        return "fail";
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("This username doesn't exist"));
        return user;
    }

    public UserResponseDTO getCurrentUserResponseDTO() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("This username doesn't exist"));
        return userMapper.toResponse(user);
    }

    public void deleteUser() {
        User user = getCurrentUser();
        user.setIsActive(false);
        userRepository.save(user);
    }

    public void deleteUserByAdmin(Long id){
        User user = userRepository.findById(id).
                orElseThrow(()-> new UserDoesNotExist("This user does not exist"));
        if(UserRole.ADMIN.equals(user.getUserRole())){
            throw new RuntimeException("You can not delete other admin");
        }
        user.setIsActive(false);
        userRepository.save(user);
    }

    public boolean isActive(User user) {
        if (!user.getIsActive()) {
            throw new UserDoesNotExist("This user does not exist");
        }
        return user.getIsActive();
    }
}
