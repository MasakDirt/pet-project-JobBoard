package com.board.job.controller;

import com.board.job.model.dto.role.RoleRequest;
import com.board.job.model.entity.Role;
import com.board.job.model.mapper.RoleMapper;
import com.board.job.service.RoleService;
import com.board.job.service.UserService;
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

import java.io.IOException;
import java.util.stream.Collectors;

import static com.board.job.controller.ControllerHelper.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/roles")
@PreAuthorize("@authRolesService.hasRole(authentication.name, 'ADMIN')")
public class RoleController {
    private final RoleService roleService;
    private final RoleMapper mapper;
    private final UserService userService;

    @GetMapping
    public ModelAndView getAll(Authentication authentication, ModelMap map) {
        var roles = roleService.getAll()
                .stream()
                .map(mapper::getRoleResponseFromRole)
                .collect(Collectors.toSet());
        log.info("=== GET-ROLES === {} === {}", getAuthorities(authentication), authentication.getName());
        map.addAttribute("roles", roles);

        return new ModelAndView("roles-list", map);
    }

    @GetMapping("/user/{user-id}")
    public ModelAndView getAllUserRoles(@PathVariable(value = "user-id") long userId, Authentication authentication,
                                        ModelMap map) {
        var roles = roleService.getAllByUserId(userId)
                .stream()
                .map(mapper::getRoleResponseFromRole)
                .collect(Collectors.toSet());
        log.info("=== GET-USER-ROLES === {} === {}", getAuthorities(authentication), authentication.getName());
        map.addAttribute("roles", roles);
        map.addAttribute("userId", userId);
        map.addAttribute("userName", userService.readById(userId).getName());

        return new ModelAndView("user-roles-get", map);
    }

    @GetMapping("/{id}")
    public ModelAndView getRole(@PathVariable long id, Authentication authentication, ModelMap map) {
        map.addAttribute("role", mapper.getRoleResponseFromRole(roleService.readById(id)));
        log.info("=== GET-ROLE-ID === {} === {}", getAuthorities(authentication), authentication.getName());
        return new ModelAndView("role-get", map);
    }

    @GetMapping("/create")
    public ModelAndView createRequest(ModelMap map) {
        map.addAttribute("roleRequest", new RoleRequest());

        return new ModelAndView("role-create", map);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid RoleRequest roleRequest, Authentication authentication,
                       HttpServletResponse response) {

        roleService.create(roleRequest.getName());
        log.info("=== POST-ROLE === {} === {}", getAuthorities(authentication), authentication.getName());

        sendRedirectAndCheckForError(response, "/api/roles");
    }

    @GetMapping("/user/{user-id}/add-role")
    public ModelAndView addUserRoleRequest(@PathVariable(value = "user-id") long userId, ModelMap map) {
        map.addAttribute("userId", userId);
        map.addAttribute("roleRequest", new RoleRequest());
        map.addAttribute("roles", roleService.getAll().stream().map(Role::getName));

        return new ModelAndView("role-user-add", map);
    }

    @PostMapping("/user/{user-id}/add-role")
    @ResponseStatus(HttpStatus.CREATED)
    public void addUserRole(@PathVariable(value = "user-id") long userId, @Valid RoleRequest roleRequest,
                            Authentication authentication, HttpServletResponse response) {
        userService.addUserRole(userId, roleRequest.getName());
        log.info("=== POST-USER-ADD-ROLE === {} === {}", getAuthorities(authentication), authentication.getName());

        sendRedirectAndCheckForError(response, "/api/roles/user/" + userId);
    }

    @GetMapping("/{id}/update")
    public ModelAndView updateRequest(@PathVariable long id, ModelMap map) {
        map.addAttribute("id", id);
        map.addAttribute("roleRequest", new RoleRequest());

        return new ModelAndView("role-update", map);
    }

    @PostMapping("/{id}/update")
    public void update(@PathVariable long id, @Valid RoleRequest roleRequest,
                       Authentication authentication, HttpServletResponse response) throws IOException {
        roleService.update(id, roleRequest.getName());
        log.info("=== PUT-ROLE === {} === {}", getAuthorities(authentication), authentication.getName());

        sendRedirectAndCheckForError(response, "/api/roles");
    }

    @GetMapping("/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id, Authentication authentication, HttpServletResponse response) throws IOException {
        roleService.delete(id);
        log.info("=== DELETE-ROLE === {} === {}", getAuthorities(authentication), authentication.getName());

        sendRedirectAndCheckForError(response, "/api/roles");
    }

    @GetMapping("/user/{user-id}/delete-role/{role-name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserRole(@PathVariable(name = "user-id") long userId,
                               @PathVariable(name = "role-name") String roleName, Authentication authentication,
                               HttpServletResponse response) {
        userService.deleteUserRole(userId, roleName);
        log.info("=== DELETE-USER-ROLE === {} === {}", getAuthorities(authentication), authentication.getName());

        sendRedirectAndCheckForError(response, "/api/roles/user/" + userId);
    }
}
