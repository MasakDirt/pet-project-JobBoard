package com.board.job.controller;

import com.board.job.model.dto.role.RoleRequest;
import com.board.job.model.dto.role.RoleResponse;
import com.board.job.model.mapper.RoleMapper;
import com.board.job.service.RoleService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import static com.board.job.controller.AuthoritiesHelper.getAuthorities;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {
    private final RoleService roleService;
    private final RoleMapper mapper;

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
    public Set<RoleResponse> getAllUserRoles(@PathVariable(value = "user-id") long userId, Authentication authentication) {
        var roles = roleService.getAllByUserId(userId)
                .stream()
                .map(mapper::getRoleResponseFromRole)
                .collect(Collectors.toSet());
        log.info("=== GET-USER-ROLES === {} === {}", getAuthorities(authentication), authentication.getName());

        return roles;
    }

    @GetMapping("/{id}")
    public ModelAndView getRole(@PathVariable long id, Authentication authentication, ModelMap map) {
        var role = mapper.getRoleResponseFromRole(roleService.readById(id));
        log.info("=== GET-ROLE-ID === {} === {}", getAuthorities(authentication), authentication.getName());
        map.addAttribute("role", role);
        return new ModelAndView("role-get", map);
    }

    @GetMapping("/name")
    public RoleResponse getRoleByName(@RequestParam String name, Authentication authentication) {
        var role = mapper.getRoleResponseFromRole(roleService.readByName(name));
        log.info("=== GET-ROLE-NAME === {} === {}", getAuthorities(authentication), authentication.getName());

        return role;
    }

    @GetMapping("/create")
    public ModelAndView createRequest(ModelMap map) {
        map.addAttribute("roleRequest", new RoleRequest());

        return new ModelAndView("role-create", map);
    }

    @PostMapping
    public void create(@Valid RoleRequest roleRequest, Authentication authentication,
                       HttpServletResponse response) throws IOException {

        roleService.create(roleRequest.getName());
        log.info("=== POST-ROLE === {} === {}", getAuthorities(authentication), authentication.getName());

        response.sendRedirect("/api/roles");
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

        response.sendRedirect("/api/roles");
    }

    @GetMapping("/{id}/delete")
    public void delete(@PathVariable long id, Authentication authentication, HttpServletResponse response) throws IOException {
        roleService.delete(id);
        log.info("=== DELETE-ROLE === {} === {}", getAuthorities(authentication), authentication.getName());

        response.sendRedirect("/api/roles");
    }
}
