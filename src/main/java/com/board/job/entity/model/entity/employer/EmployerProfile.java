package com.board.job.entity.model.entity.employer;

import com.board.job.entity.model.entity.Image;
import com.board.job.entity.model.entity.User;
import com.board.job.entity.model.entity.Vacancy;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Table
@Entity
@Getter
@Setter
public class EmployerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "The employer name cannot be blank")
    @Column(name = "employer_name")
    private String employerName;

    @NotNull
    @Column(name = "position", nullable = false)
    private String positionInCompany;

    @NotNull
    @Column(name = "company", nullable = false)
    private String companyName;

    @NotNull
    @Pattern(regexp = "^@.*", message = "The telegram account must started with '@' character")
    private String telegram;

    @NotNull
    @Pattern(regexp = "^(\\\\+\\\\d{2})?\\\\(?\\\\d{3}\\\\)?-?\\\\d{3}-?\\\\d{2}-?\\\\d{2}$",
            message = "Phone number must contain numbers and look like - 068xxx90xx or +38(068)-xxx-90-xx")
    private String phone;

    @NotNull
    @Column(name = "linked_in", nullable = false)
    private String linkedInProfile;

    @NotNull
    @JsonBackReference
    @JoinColumn(name = "owner_id")
    @OneToOne(fetch = FetchType.EAGER)
    private User owner;

    @NotNull
    @JsonBackReference
    @JoinColumn(name = "photo_id")
    @OneToOne(fetch = FetchType.EAGER)
    private Image photo;

    @NotNull
    @JsonManagedReference
    @OneToMany(mappedBy = "employerProfile", cascade = CascadeType.ALL)
    private List<Vacancy> vacancies;

    public EmployerProfile() {
        employerName = Objects.requireNonNull(owner.getFirstName()) + Objects.requireNonNull(owner.getLastName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployerProfile that = (EmployerProfile) o;
        return id == that.id && Objects.equals(employerName, that.employerName) && Objects.equals(positionInCompany, that.positionInCompany)
                && Objects.equals(companyName, that.companyName) && Objects.equals(telegram, that.telegram)
                && Objects.equals(phone, that.phone) && Objects.equals(linkedInProfile, that.linkedInProfile) && Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, employerName, positionInCompany, companyName, telegram, phone, linkedInProfile, owner);
    }

    @Override
    public String toString() {
        return "EmployerProfile{" +
                "id=" + id +
                ", employerName='" + employerName + '\'' +
                ", positionInCompany='" + positionInCompany + '\'' +
                ", companyName='" + companyName + '\'' +
                ", telegram='" + telegram + '\'' +
                ", phone='" + phone + '\'' +
                ", linkedInProfile='" + linkedInProfile + '\'' +
                ", owner=" + owner +
                '}';
    }
}
