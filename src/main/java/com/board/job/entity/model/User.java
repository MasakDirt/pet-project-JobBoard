package com.board.job.entity.model;

import com.board.job.entity.model.candidate.CandidateContacts;
import com.board.job.entity.model.candidate.CandidateProfile;
import com.board.job.entity.model.employer.EmployerCompany;
import com.board.job.entity.model.employer.EmployerProfile;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Table
@Entity
@Getter
@Setter
@AllArgsConstructor
public class User {
    public static final String NAME_REGEXP = "[A-Z][a-z]+(-[A-Z][a-z]+){0,1}";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(nullable = false)
    @Pattern(regexp = NAME_REGEXP,
            message = "First name must start with a capital letter and followed by one or more lowercase")
    private String firstName;

    @NotNull
    @Column(nullable = false)
    @Pattern(regexp = NAME_REGEXP,
            message = "Last name must start with a capital letter and followed by one or more lowercase")
    private String lastName;

    @NotNull
    @Pattern(regexp = "[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}", message = "Must be a valid e-mail address")
    @Column(name = "e-mail", unique = true, nullable = false)
    private String email;

    @NotBlank(message = "The password cannot be 'blank'")
    @Column(nullable = false)
    private String password;

    @JsonBackReference
    @JoinColumn(name = "role_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;

    @JsonManagedReference
    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL)
    private CandidateProfile candidateProfile;

    @JsonManagedReference
    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL)
    private CandidateContacts candidateContacts;

    @JsonManagedReference
    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL)
    private EmployerProfile employerProfile;

    @JsonManagedReference
    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL)
    private EmployerCompany employerCompany;

    public User() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) &&
                Objects.equals(email, user.email) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, password);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
