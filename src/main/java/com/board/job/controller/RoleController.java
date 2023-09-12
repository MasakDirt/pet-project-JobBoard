package com.board.job.controller;

import com.board.job.model.dto.role.RoleResponse;
import com.board.job.model.mapper.RoleMapper;
import com.board.job.service.RoleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public Set<RoleResponse> getAll(Authentication authentication) {
        var roles = roleService.getAll()
                .stream()
                .map(mapper::getRoleResponseFromRole)
                .collect(Collectors.toSet());
        log.info("=== GET-ROLES === {} === {}", getAuthorities(authentication), authentication.getPrincipal());

        return roles;
    }

    @GetMapping("/user/{user-id}")
    public Set<RoleResponse> getAllUserRoles(@PathVariable(value = "user-id") long userId, Authentication authentication) {
        var roles = roleService.getAllByUserId(userId)
                .stream()
                .map(mapper::getRoleResponseFromRole)
                .collect(Collectors.toSet());
        log.info("=== GET-USER-ROLES === {} === {}", getAuthorities(authentication), authentication.getPrincipal());

        return roles;
    }

    @GetMapping("/{id}")
    public RoleResponse getRole(@PathVariable long id, Authentication authentication) {
        var role = mapper.getRoleResponseFromRole(roleService.readById(id));
        log.info("=== GET-ROLE-ID === {} === {}", getAuthorities(authentication), authentication.getPrincipal());

        return role;
    }

    @GetMapping("/name")
    public RoleResponse getRoleByName(@RequestParam String name, Authentication authentication) {
        var role = mapper.getRoleResponseFromRole(roleService.readByName(name));
        log.info("=== GET-ROLE-NAME === {} === {}", getAuthorities(authentication), authentication.getPrincipal());

        return role;
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestParam String name, Authentication authentication) {
        var role = roleService.create(name);
        log.info("=== POST-ROLE === {} === {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                String.format("Role with name %s created", role.getName())
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable long id, @RequestParam String name,
                                         Authentication authentication) {
        var oldRoleName = roleService.readById(id).getName();
        var roleNewName = roleService.update(id, name).getName();
        log.info("=== PUT-ROLE === {} === {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                String.format("Role old name %s updated to %s", oldRoleName, roleNewName)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable long id, Authentication authentication) {
        var roleName = roleService.readById(id).getName();
        roleService.delete(id);
        log.info("=== DELETE-ROLE === {} === {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                String.format("Role with name %s successfully deleted", roleName)
        );
    }
}
