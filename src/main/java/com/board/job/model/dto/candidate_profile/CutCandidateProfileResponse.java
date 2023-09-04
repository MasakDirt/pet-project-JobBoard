package com.board.job.model.dto.candidate_profile;

import com.board.job.model.entity.sample.Category;
import com.board.job.model.entity.sample.LanguageLevel;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class CutCandidateProfileResponse {
    private long id;

    @NotBlank(message = "The position cannot be 'blank'")
    private String position;

    @JsonProperty(value = "salary_expectations")
    @Min(value = 10, message = "The salary expectation must be at least 10$.")
    private int salaryExpectations;

    @JsonProperty(value = "work_experience")
    @Min(value = 0, message = "The work experience must be at least 0 years.")
    private double workExperience;

    @JsonProperty(value = "country_of_residence")
    @NotBlank(message = "The country of residence cannot be 'blank'")
    private String countryOfResidence;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "You must pick one category")
    private Category category;

    @Enumerated(EnumType.STRING)
    @JsonProperty(value = "english_level")
    @NotNull(message = "You must select your level of english")
    private LanguageLevel englishLevel;
}
