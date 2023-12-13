package com.board.job.service;

import com.board.job.exception.UserIsNotEmployer;
import com.board.job.model.entity.CustomOAuth2User;
import com.board.job.model.entity.sample.Provider;
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

import java.util.*;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public User create(User user, Set<Role> roles) {
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (!hasProvider(user)) {
            user.setProvider(Provider.LOCAL);
        }
        return userRepository.save(user);
    }

    private boolean hasProvider(User user) {
        return Objects.nonNull(user.getProvider());
    }

    public User processOAuthPostLogin(CustomOAuth2User oAuth2User) {
        Optional<User> existUser = userRepository.findByEmail(oAuth2User.getName());

        if (existUser.isEmpty()) {
            User newUser = new User();
            newUser.setEmail(oAuth2User.getName());
            newUser.setFirstName(oAuth2User.getAttributes().get("given_name").toString());
            newUser.setLastName(oAuth2User.getAttributes().get("family_name").toString());
            newUser.setPassword(oAuth2User.getAttributes().get("sub").toString());
            newUser.setProvider(Provider.GOOGLE);

           return create(newUser, Set.of(roleService.readByName("USER")));
        }
        return existUser.get();
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
        checkPasswords(oldPassword, oldUser.getPassword());

        updated.setId(oldUser.getId());
        updated.setEmail(oldUser.getEmail());

        return create(updated, new HashSet<>(roleService.getAllByUserId(oldUser.getId())));
    }

    public void checkPasswords(String oldPassword, String encodedPassword) {
        if (!passwordEncoder.matches(oldPassword, encodedPassword)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong old password!");
        }
    }

    public void delete(long id) {
        userRepository.delete(readById(id));
    }

    public void deleteUserRole(long id, String roleName) {
        User user = readById(id);
        List<Role> roles = roleService.getAllByUserId(id);

        deleteRole(roles, roleService.readByName(roleName));
        user.setRoles(new HashSet<>(roles));
        update(user);
    }

    private void deleteRole(List<Role> roles, Role toDelete) {
        roles.remove(toDelete);
    }

    public User addUserRole(long id, String roleName) {
        User user = readById(id);
        List<Role> roles = roleService.getAllByUserId(id);

        addRole(roles, roleService.readByName(roleName));
        user.setRoles(new HashSet<>(roles));
        return update(user);
    }

    private void addRole(List<Role> roles, Role adding) {
        if (!roles.contains(adding)) {
            roles.add(adding);
        }
    }

    public Pair<EmployerCompany, EmployerProfile> getEmployerData(long id) {
        User user = readById(id);
        EmployerCompany employerCompany = user.getEmployerCompany();
        EmployerProfile employerProfile = user.getEmployerProfile();

        if (userIsEmployer(employerCompany, employerProfile)) {
            return Pair.create(employerCompany, employerProfile);
        }

        throw new UserIsNotEmployer("Please create your employer profile, then you will be permitted to create a vacancy");
    }

    private boolean userIsEmployer(EmployerCompany employerCompany, EmployerProfile employerProfile) {
        return !Objects.isNull(employerCompany) && !Objects.isNull(employerProfile);
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
