package com.board.job.model.entity;

import com.board.job.model.entity.employer.EmployerCompany;
import com.board.job.model.entity.employer.EmployerProfile;
import com.board.job.model.entity.sample.Category;
import com.board.job.model.entity.sample.JobDomain;
import com.board.job.model.entity.sample.LanguageLevel;
import com.board.job.model.entity.sample.WorkMode;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "vacancies")
public class Vacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "looking_for", nullable = false)
    @NotBlank(message = "Here must be the candidate that you are looking for.")
    private String lookingFor;

    @NotNull
    @Enumerated(EnumType.STRING)
    private JobDomain domain;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Your vacancy description must contain detailed description of this.")
    private String detailDescription;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;

    @NotNull
    @Enumerated(EnumType.STRING)
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
    @Enumerated(EnumType.STRING)
    @Column(name = "english_level")
    private LanguageLevel englishLevel;

    @Column(name = "posted_at")
    @CreationTimestamp
    private LocalDateTime postedAt;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "employer_profile_id")
    private EmployerProfile employerProfile;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "employer_company_id")
    private EmployerCompany employerCompany;

    @JsonManagedReference
    @OneToMany(mappedBy = "vacancy", cascade = CascadeType.ALL)
    private List<Messenger> messengerForVacanciesReplies;

    public Vacancy() {
        postedAt = LocalDateTime.now();
    }

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
