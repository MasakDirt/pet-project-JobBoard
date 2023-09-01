package com.board.job.controller;

import com.board.job.model.dto.user.UserCreateRequest;
import com.board.job.model.dto.user.UserResponse;
import com.board.job.model.dto.user.UserUpdateRequest;
import com.board.job.model.dto.user.UserUpdateRequestWithPassword;
import com.board.job.model.mapper.UserMapper;
import com.board.job.service.RoleService;
import com.board.job.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

import static com.board.job.controller.AuthoritiesHelper.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final RoleService roleService;
    private final UserMapper mapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Set<UserResponse> getAll(Authentication authentication) {
        var users = userService.getAll()
                .stream()
                .map(mapper::getUserResponseFromUser)
                .collect(Collectors.toSet());
        log.info("=== GET-USERS === {} === {}", getAuthorities(authentication), authentication.getPrincipal());

        return users;
    }

    @GetMapping("/{id}")
    @PreAuthorize("@userAuthService.isUserAdminOrUsersSameById(#id, authentication.principal)")
    public UserResponse getById(@PathVariable long id, Authentication authentication) {
        var user = mapper.getUserResponseFromUser(userService.readById(id));
        log.info("=== GET-USER-ID === {} === {}", getAuthorities(authentication), authentication.getPrincipal());

        return user;
    }

    @GetMapping("/email")
    @PreAuthorize("@userAuthService.isUserAdminOrUsersSameByEmail(#email, authentication.principal)")
    public UserResponse getByEmail(@RequestParam String email, Authentication authentication) {
        var user = mapper.getUserResponseFromUser(userService.readByEmail(email));
        log.info("=== GET-USER-EMAIL === {} === {}", getAuthorities(authentication), authentication.getPrincipal());

        return user;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createAdmin(@RequestBody @Valid UserCreateRequest createRequest,
                                              Authentication authentication) {
        var created = userService.create(
                mapper.getUserFromUserCreate(createRequest),
                Set.of(roleService.readByName("ADMIN"))
        );
        log.info("=== POST-USER-ADMIN === {} === {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                String.format("User admin with name %s successfully created", created.getName())
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("@userAuthService.isUsersSame(#id, authentication.principal)")
    public ResponseEntity<String> update(@PathVariable long id, Authentication authentication,
                                         @RequestBody @Valid UserUpdateRequestWithPassword request) {

        var updated = userService.updateNames(id,
                mapper.getUserFromUserUpdateRequestPass(request), request.getOldPassword());
        log.info("=== PUT-USER === {} === {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.ok(String.format("User with name %s successfully updated", updated.getName()));
    }

    @PutMapping("/names/{id}")
    @PreAuthorize("@userAuthService.isUsersSame(#id, authentication.principal)")
    public ResponseEntity<String> updateNames(@PathVariable long id, Authentication authentication,
                                         @RequestBody @Valid UserUpdateRequest request) {

        var updated = userService.updateNames(id, mapper.getUserFromUserUpdateRequest(request));
        log.info("=== PUT-USER === {} === {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.ok(String.format("User with name %s successfully updated", updated.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@userAuthService.isUsersSame(#id, authentication.principal)")
    public ResponseEntity<String> delete(@PathVariable long id, Authentication authentication) {
        var user = userService.readById(id);
        userService.delete(id);
        log.info("=== DELETE-USER === {} === {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.ok(String.format("User with name %s successfully deleted", user.getName()));
    }
}
