package com.board.job.service.authorization;

import com.board.job.model.entity.Role;
import com.board.job.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthRolesService {
    private final RoleService roleService;

    public boolean hasRole(String email, String role) {
        return roleService.getAllByEmail(email).contains(roleService.readByName(role));
    }

    public boolean hasAnyRole(String email, String... roles) {
        Set<String> userRoles = roleService.getAllByEmail(email)
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        for (String role : roles) {
            if (userRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }
}
