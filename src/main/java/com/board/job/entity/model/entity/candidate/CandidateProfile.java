package com.board.job.entity.model.entity.candidate;

import com.board.job.entity.model.entity.Feedback;
import com.board.job.entity.model.entity.User;
import com.board.job.entity.sample.Category;
import com.board.job.entity.sample.LanguageLevel;
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

@Table
@Entity
@Getter
@Setter
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

    @NotBlank(message = "The country of residence cannot be 'blank'")
    @Column(name = "country_of_residence")
    private String countryOfResidence;

    @NotBlank(message = "The city of residence cannot be 'blank'")
    @Column(name = "city_of_residence")
    private String cityOfResidence;

    @NotNull
    private Category category;

    @NotNull
    @Column(name = "english_level")
    private LanguageLevel englishLevel;

    @NotNull
    @Column(name = "ukrainian_level")
    private LanguageLevel ukrainianLevel;

    @NotNull
    @Column(name = "experience_explanation")
    private String experienceExplanation;

    @NotNull
    private String achievements;

    @NotNull
    @JsonBackReference
    @JoinColumn(name = "owner_id")
    @OneToOne(fetch = FetchType.EAGER)
    private User owner;

    @JsonManagedReference
    @OneToMany(mappedBy = "candidateProfile", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks;

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
