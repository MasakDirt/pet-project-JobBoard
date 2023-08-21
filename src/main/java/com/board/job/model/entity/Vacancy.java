package com.board.job.model.entity;

import com.board.job.model.entity.employer.EmployerCompany;
import com.board.job.model.entity.employer.EmployerProfile;
import com.board.job.model.entity.sample.Category;
import com.board.job.model.entity.sample.JobDomain;
import com.board.job.model.entity.sample.LanguageLevel;
import com.board.job.model.entity.sample.WorkMode;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Table
@Entity
@Getter
@Setter
public class Vacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "looking_for", nullable = false)
    @NotBlank(message = "Here must be the candidate that you are looking for.")
    private String lookingFor;

    @NotNull
    private JobDomain domain;

    @Column(name = "description", nullable = false)
    @NotBlank(message = "Your vacancy description must contain detailed description of this.")
    private String detailDescription;

    @NotNull
    private Category category;

    @NotNull
    @Column(name = "work_mode", nullable = false)
    private WorkMode workMode;

    @NotBlank(message = "You should declare country where candidate must be work.")
    private String country;

    private String city;

    @Column(name = "salary_from")
    private int salaryFrom;

    @Column(name = "salary_to")
    private int salaryTo;

    @Column(name = "work_experience")
    @Min(value = 0, message = "The work experience must be at least 0 years.")
    private double workExperience;

    @NotNull
    @Column(name = "english_level")
    private LanguageLevel englishLevel;

    @JsonBackReference
    @JoinColumn(name = "employer_profile_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private EmployerProfile employerProfile;

    @JsonBackReference
    @JoinColumn(name = "employer_company_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private EmployerCompany employerCompany;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vacancy vacancy = (Vacancy) o;
        return id == vacancy.id && salaryFrom == vacancy.salaryFrom && salaryTo == vacancy.salaryTo
                && Double.compare(vacancy.workExperience, workExperience) == 0 && Objects.equals(lookingFor, vacancy.lookingFor)
                && domain == vacancy.domain && Objects.equals(detailDescription, vacancy.detailDescription)
                && category == vacancy.category && workMode == vacancy.workMode
                && Objects.equals(country, vacancy.country) && Objects.equals(city, vacancy.city) && englishLevel == vacancy.englishLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lookingFor, domain, detailDescription, category, workMode, country, city, salaryFrom, salaryTo, workExperience, englishLevel);
    }

    @Override
    public String toString() {
        return "Vacancy{" +
                "id=" + id +
                ", lookingFor='" + lookingFor + '\'' +
                ", domain=" + domain +
                ", detailDescription='" + detailDescription + '\'' +
                ", category=" + category +
                ", workMode=" + workMode +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", salaryFrom=" + salaryFrom +
                ", salaryTo=" + salaryTo +
                ", workExperience=" + workExperience +
                ", englishLevel=" + englishLevel +
                '}';
    }
}
