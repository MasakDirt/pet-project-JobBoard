package com.board.job.model.entity;

import com.board.job.model.entity.candidate.CandidateContact;
import com.board.job.model.entity.candidate.CandidateProfile;
import com.board.job.model.entity.employer.EmployerCompany;
import com.board.job.model.entity.employer.EmployerProfile;
import com.board.job.model.entity.sample.Provider;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
    public static final String NAME_REGEXP = "^(?:[A-Z][a-z]+(?:-[A-Z][a-z]+)?|[А-ЩЬЮЯҐЄІЇІ][а-щьюяґєіїі']+(?:-[А-ЩЬЮЯҐЄІЇІ][а-щьюяґєіїі']+)?$)";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "Write the first name!")
    @Column(nullable = false)
    @Pattern(regexp = NAME_REGEXP,
            message = "First name must start with a capital letter and followed by one or more lowercase")
    private String firstName;

    @NotNull(message = "Write the last name!")
    @Column(nullable = false)
    @Pattern(regexp = NAME_REGEXP,
            message = "Last name must start with a capital letter and followed by one or more lowercase")
    private String lastName;

    @NotNull
    @Pattern(regexp = "[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}", message = "Must be a valid e-mail address")
    @Column(name = "e_mail", unique = true, nullable = false)
    private String email;

    @NotBlank(message = "The password cannot be 'blank'")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @JsonBackReference
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @JsonManagedReference
    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL)
    private CandidateProfile candidateProfile;

    @JsonManagedReference
    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL)
    private CandidateContact candidateContact;

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getName() {
        return String.format("%s %s", firstName, lastName);
    }
}
