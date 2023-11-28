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
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import static com.board.job.controller.ControllerHelper.redirectionError;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final UserMapper mapper;
    private final UserService userService;
    private final RoleService roleService;

    @GetMapping("/login")
    public ModelAndView showLoginForm(ModelMap map) {
        map.addAttribute("loginRequest", new LoginRequest());
        return new ModelAndView("login", map);
    }

    @PostMapping("/login")
    public void login(@Valid LoginRequest loginRequest) {
        var user = userService.readByEmail(loginRequest.getEmail());
        userService.checkPasswords(loginRequest.getPassword(), user.getPassword());

        log.info("=== POST-LOGIN === auth - {} === time - {}.", user.getEmail(), LocalDateTime.now());
    }

    @GetMapping("/register")
    public ModelAndView createForm(ModelMap model) {
        model.addAttribute("createRequest", new UserCreateRequest());
        return new ModelAndView("register", model);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid UserCreateRequest createRequest, HttpServletResponse response) {
        var user = userService.create(mapper.getUserFromUserCreate(createRequest), Set.of(roleService.readByName("USER")));

        log.info("=== POST-REGISTER === register - {} === time - {}.", user.getUsername(), LocalDateTime.now());

        try {
            response.sendRedirect("/api/auth/login");
        } catch (IOException e) {
            log.error("Error while sending redirect - {}", e.getMessage());
            redirectionError();
        }
    }
}
