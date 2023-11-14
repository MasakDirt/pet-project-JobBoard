package com.board.job.model.entity.candidate;

import com.board.job.model.entity.Messenger;
import com.board.job.model.entity.User;
import com.board.job.model.entity.sample.Category;
import com.board.job.model.entity.sample.LanguageLevel;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "candidate_profiles")
public class CandidateProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "The position cannot be 'blank'")
    private String position;

    @Column(name = "salary_expectations")
    @Min(value = 10, message = "The salary expectation must be at least 10$.")
    private int salaryExpectations;

    @Column(name = "work_experience")
    @Min(value = 0, message = "The work experience must be at least 0 years.")
    private double workExperience;

    @Column(name = "country_of_residence")
    @NotBlank(message = "The country of residence cannot be 'blank'")
    private String countryOfResidence;

    @Column(name = "city_of_residence")
    @NotBlank(message = "The city of residence cannot be 'blank'")
    private String cityOfResidence;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "You must pick one category")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "english_level")
    @NotNull(message = "You must select your level of english")
    private LanguageLevel englishLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "ukrainian_level")
    @NotNull(message = "You must select your level of ukrainian")
    private LanguageLevel ukrainianLevel;

    @Column(name = "experience_explanation", columnDefinition = "TEXT")
    @NotBlank(message = "Tell your experience here, if you have no, write about your study")
    private String experienceExplanation;

    @Column(columnDefinition = "TEXT")
    private String achievements;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "owner_id")
    private User owner;

    @JsonManagedReference
    @OneToMany(mappedBy = "candidateProfile", cascade = CascadeType.ALL)
    private List<Messenger> messengerForVacanciesReplies;

    public CandidateProfile() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CandidateProfile that = (CandidateProfile) o;
        return id == that.id && salaryExpectations == that.salaryExpectations && Double.compare(that.workExperience, workExperience) == 0
                 && Objects.equals(position, that.position) && Objects.equals(countryOfResidence, that.countryOfResidence) &&
                Objects.equals(cityOfResidence, that.cityOfResidence) && category == that.category
                && englishLevel == that.englishLevel && ukrainianLevel == that.ukrainianLevel && Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, position, salaryExpectations, workExperience, countryOfResidence,
                cityOfResidence, category, englishLevel, ukrainianLevel, owner);
    }

    @Override
    public String toString() {
        return "CandidateProfile{" +
                "id=" + id +
                ", position='" + position + '\'' +
                ", salaryExpectations=" + salaryExpectations +
                ", workExperience=" + workExperience +
                ", countryOfResidence='" + countryOfResidence + '\'' +
                ", cityOfResidence='" + cityOfResidence + '\'' +
                ", category=" + category +
                ", englishLevel=" + englishLevel +
                ", ukrainianLevel=" + ukrainianLevel +
                ", owner=" + owner +
                '}';
    }
}
