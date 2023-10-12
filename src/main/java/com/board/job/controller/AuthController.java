package com.board.job.controller;

import com.board.job.model.dto.login.LoginRequest;
import com.board.job.model.dto.user.UserCreateRequest;
import com.board.job.model.mapper.UserMapper;
import com.board.job.service.RoleService;
import com.board.job.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final UserMapper mapper;
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public ModelAndView showLoginForm(ModelMap model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return new ModelAndView("login", model);
    }

    @PostMapping("/login")
    public void login(@Valid LoginRequest loginRequest) {
        var user = userService.readByEmail(loginRequest.getEmail());

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(401), "Wrong password");
        }
        log.info("=== POST-LOGIN === auth - {} === time - {}.", user.getEmail(), LocalDateTime.now());
    }

    @GetMapping("/register")
    public ModelAndView createForm(ModelMap model) {
        model.addAttribute("createRequest", new UserCreateRequest());
        return new ModelAndView("register", model);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid UserCreateRequest createRequest, HttpServletResponse response) throws IOException {
        var user = userService.create(mapper.getUserFromUserCreate(createRequest), Set.of(roleService.readByName("USER")));

        log.info("=== POST-REGISTER === register - {} === time - {}.", user.getUsername(), LocalDateTime.now());
        response.sendRedirect("/api/auth/login");
    }
}
