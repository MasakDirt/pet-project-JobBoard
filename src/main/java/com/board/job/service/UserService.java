package com.board.job.service;

import com.board.job.model.entity.Role;
import com.board.job.model.entity.User;
import com.board.job.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User create(User user, Role role) {
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User readById(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User with id %d not found"));
    }

    public User readByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new EntityNotFoundException(String.format("User with email %s not found", email)));
    }

    public User update(User updated, String oldPassword) {
        var oldUser = readById(updated.getId());

        if (!passwordEncoder.matches(oldPassword, oldUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong old password!");
        }

        updated.setId(oldUser.getId());
        updated.setEmail(oldUser.getEmail());

        return create(updated, oldUser.getRole());
    }

    public void delete(long id) {
        userRepository.delete(readById(id));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public List<User> getAllByRoleName(String name) {
        return userRepository.findAllByRoleName(name);
    }

    public List<User> getAllByFirstName(String firstName) {
        return userRepository.findAllByFirstName(firstName);
    }

    public List<User> getAllByLastName(String lastName) {
        return userRepository.findAllByLastName(lastName);
    }

    public List<User> getAllByFirstNameAndLastName(String firstName, String lastName) {
        return userRepository.findAllByFirstNameAndLastName(firstName, lastName);
    }
}
