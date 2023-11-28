package com.board.job.model.dto.vacancy;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacancyRequest {
    private long id;

    @JsonProperty(value = "looking_for")
    @NotBlank(message = "Here must be the candidate that you are looking for.")
    private String lookingFor;

    private String domain;

    @JsonProperty(value = "description")
    @NotBlank(message = "Your vacancy description must contain detailed description of this.")
    private String detailDescription;

    private String category;

    private String workMode;

    @NotBlank(message = "You should declare country where candidate must be work.")
    private String country;

    private String city;

    @JsonProperty(value = "salary_from")
    private int salaryFrom;

    @JsonProperty(value = "salary_to")
    private int salaryTo;

    @JsonProperty(value = "work_experience")
    @Min(value = 0, message = "The work experience must be at least 0 years.")
    private double workExperience;

    @JsonProperty(value = "english_level")
    private String englishLevel;
}
