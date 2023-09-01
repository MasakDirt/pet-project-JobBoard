package com.board.job.controller;

import com.board.job.model.dto.login.LoginRequest;
import com.board.job.model.dto.user.UserCreateRequest;
import com.board.job.model.dto.user.UserResponse;
import com.board.job.model.mapper.UserMapper;
import com.board.job.service.RoleService;
import com.board.job.service.UserService;
import com.board.job.util.JwtUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtUtils jwtUtils;
    private final UserMapper mapper;
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public String login(@RequestBody @Valid LoginRequest loginRequest) {
        var userDetails = userService.readByEmail(loginRequest.getEmail());

        if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(401), "Wrong password");
        }

        log.info("=== POST-LOGIN === auth - {} === time - {}.", userDetails.getEmail(), LocalDateTime.now());

        return jwtUtils.generateTokenFromEmail(userDetails.getEmail());
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@RequestBody @Valid UserCreateRequest createRequest) {
        var user = userService.create(mapper.getUserFromUserCreate(createRequest), Set.of(roleService.readByName("USER")));

        log.info("=== POST-REGISTER === register - {} === time - {}.", user.getUsername(), LocalDateTime.now());
        return mapper.getUserResponseFromUser(user);
    }
}
