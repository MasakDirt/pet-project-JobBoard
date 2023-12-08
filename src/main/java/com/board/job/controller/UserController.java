package com.board.job.controller;

import com.board.job.model.dto.user.UserCreateRequest;
import com.board.job.model.dto.user.UserUpdateRequest;
import com.board.job.model.dto.user.UserUpdateRequestWithPassword;
import com.board.job.model.entity.sample.Provider;
import com.board.job.model.mapper.UserMapper;
import com.board.job.service.RoleService;
import com.board.job.service.UserService;
import com.board.job.service.authorization.UserAuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Set;
import java.util.stream.Collectors;

import static com.board.job.controller.ControllerHelper.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final RoleService roleService;
    private final UserMapper mapper;
    private final UserAuthService userAuthService;

    @GetMapping
    @PreAuthorize("@authRolesService.hasRole(authentication.name, 'ADMIN')")
    public ModelAndView getAll(Authentication authentication, ModelMap map) {
        map.addAttribute("users", userService.getAll()
                .stream()
                .map(mapper::getUserResponseFromUser)
                .collect(Collectors.toSet()));
        log.info("=== GET-USERS === {} === {}", getAuthorities(authentication), authentication.getName());

        return new ModelAndView("users-list", map);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@userAuthService.isUserAdminOrUsersSameById(#id, authentication.name)")
    public ModelAndView getById(@PathVariable long id, Authentication authentication, ModelMap map) {
        var user = userService.readById(id);
        var userResponse = mapper.getUserResponseFromUser(user);
        map.addAttribute("isAdmin", userAuthService.isAdmin(userResponse.getEmail()));
        map.addAttribute("user", userResponse);
        map.addAttribute("isGoogleUser", user.getProvider().equals(Provider.GOOGLE));
        log.info("=== GET-USER-ID === {} === {}", getAuthorities(authentication), authentication.getName());

        return new ModelAndView("user-get", map);
    }

    @GetMapping("/email")
    @PreAuthorize("@userAuthService.isUserAdminOrUsersSameByEmail(#email, authentication.name)")
    public ModelAndView getByEmail(@RequestParam String email, Authentication authentication, ModelMap map) {
        var user = mapper.getUserResponseFromUser(userService.readByEmail(email));
        map.addAttribute("isAdmin", userAuthService.isAdmin(user.getEmail()));
        map.addAttribute("user", user);
        log.info("=== GET-USER-EMAIL === {} === {}", getAuthorities(authentication), authentication.getName());

        return new ModelAndView("user-get", map);
    }

    @GetMapping("/create-admin")
    @PreAuthorize("@authRolesService.hasRole(authentication.name, 'ADMIN')")
    public ModelAndView getCreateRequest(ModelMap map) {
        map.addAttribute("createRequest", new UserCreateRequest());

        return new ModelAndView("user-create", map);
    }

    @PostMapping
    @PreAuthorize("@authRolesService.hasRole(authentication.name, 'ADMIN')")
    public void createAdmin(@Valid UserCreateRequest createRequest, Authentication authentication,
                            HttpServletResponse response) {

        var created = userService.create(
                mapper.getUserFromUserCreate(createRequest),
                Set.of(roleService.readByName("ADMIN")));
        log.info("=== POST-USER-ADMIN === {} === {}", getAuthorities(authentication), authentication.getName());

        sendRedirectAndCheckForError(response, "/api/users/" + created.getId());
    }

    @GetMapping("/{id}/update")
    @PreAuthorize("@userAuthService.isUserAdminOrUsersSameById(#id, authentication.name)")
    public ModelAndView getUpdateRequest(@PathVariable long id, ModelMap map) {
        map.addAttribute("id", id);
        map.addAttribute("updateRequest", new UserUpdateRequestWithPassword());

        return new ModelAndView("user-update", map);
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("@userAuthService.isUserAdminOrUsersSameById(#id, authentication.name)")
    public void update(@PathVariable long id, Authentication authentication,
                       @Valid UserUpdateRequestWithPassword request, HttpServletResponse response) {

        userService.update(id,
                mapper.getUserFromUserUpdateRequestPass(request), request.getOldPassword());
        log.info("=== PUT-USER === {} === {}", getAuthorities(authentication), authentication.getName());

        sendRedirectAndCheckForError(response, "/api/users/" + id);
    }

    @GetMapping("/names/{id}/update")
    @PreAuthorize("@userAuthService.isUserAdminOrUsersSameById(#id, authentication.name)")
    public ModelAndView getUpdateNamesRequest(@PathVariable long id, ModelMap map) {
        map.addAttribute("id", id);
        map.addAttribute("updateRequest", new UserUpdateRequest());

        return new ModelAndView("user-update-names", map);
    }

    @PostMapping("/names/{id}/update")
    @PreAuthorize("@userAuthService.isUserAdminOrUsersSameById(#id, authentication.name)")
    public void updateNames(@PathVariable long id, Authentication authentication,
                            @Valid UserUpdateRequest request, HttpServletResponse response) {

        userService.updateNames(id, mapper.getUserFromUserUpdateRequest(request));
        log.info("=== PUT-USER-NAMES === {} === {}", getAuthorities(authentication), authentication.getName());

        sendRedirectAndCheckForError(response, "/api/users/" + id);
    }

    @GetMapping("/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@userAuthService.isUserAdminOrUsersSameById(#id, authentication.name)")
    public void delete(@PathVariable long id, Authentication authentication, HttpServletResponse response) {
        userService.delete(id);
        log.info("=== DELETE-USER === {} === {}", getAuthorities(authentication), authentication.getName());

        sendRedirectAndCheckForError(response, "/api/auth/login");
    }
}
