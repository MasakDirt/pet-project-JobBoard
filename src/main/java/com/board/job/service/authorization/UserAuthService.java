package com.board.job.service.authorization;

import com.board.job.model.entity.User;
import com.board.job.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserAuthService {
    private final UserService userService;

    public boolean isUsersSame(long id, String email) {
        return getUser(email).getId() == id;
    }

    public boolean isUserAdminOrUsersSameById(long id, String email) {
        return isAdmin(email) || isUsersSame(id, email);
    }

    public boolean isUserAdminOrUsersSameByEmail(String email, String authEmail) {
        return isAdmin(authEmail) || getUser(authEmail).getEmail().equals(email);
    }

    public boolean isAdmin(String email) {
       return getUser(email).getRoles()
                .stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));
    }

    public User getUser(String email) {
        return userService.readByEmail(email);
    }
}
