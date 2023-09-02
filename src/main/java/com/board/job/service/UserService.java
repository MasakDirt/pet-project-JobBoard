package com.board.job.service;

import com.board.job.exception.UserIsNotEmployer;
import com.board.job.model.entity.Role;
import com.board.job.model.entity.User;
import com.board.job.model.entity.employer.EmployerCompany;
import com.board.job.model.entity.employer.EmployerProfile;
import com.board.job.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.cassandra.utils.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public User create(User user, Set<Role> roles) {
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User readById(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User not found"));
    }

    public User readByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new EntityNotFoundException(String.format("User with email %s not found", email)));
    }

    public User update(User updated) {
        readById(updated.getId());

        return userRepository.save(updated);
    }

    public User updateNames(long id, User updated) {
        var user = readById(id);
        user.setFirstName(updated.getFirstName());
        user.setLastName(updated.getLastName());

        return userRepository.save(user);
    }

    public User update(long id, User updated, String oldPassword) {
        var oldUser = readById(id);

        if (!passwordEncoder.matches(oldPassword, oldUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong old password!");
        }

        updated.setId(oldUser.getId());
        updated.setEmail(oldUser.getEmail());

        return create(updated, new HashSet<>(roleService.getAllByUserId(oldUser.getId())));
    }

    public void delete(long id) {
        userRepository.delete(readById(id));
    }

    public User updateUserRolesAndGetUser(long id, String roleName) {
        var user = readById(id);
        var role = roleService.readByName(roleName);
        var roles = roleService.getAllByUserId(id);

        if (!roles.contains(role)) {
            roles.add(role);

            user.setRoles(new HashSet<>(roles));
            return update(user);
        }

        return user;
    }

    public Pair<EmployerCompany, EmployerProfile> getEmployerData(long id) {
        var user = readById(id);
        var employerCompany = user.getEmployerCompany();
        var employerProfile = user.getEmployerProfile();

        if (employerCompany == null || employerProfile == null) {
            throw new UserIsNotEmployer("Please create your employer profile, then you will be permitted to create a vacancy");
        }

        return Pair.create(employerCompany, employerProfile);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public List<User> getAllByRoleName(String name) {
        return userRepository.findAllByRolesName(name);
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
