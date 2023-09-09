package com.board.job.model.dto.vacancy;

import com.board.job.model.entity.sample.Category;
import com.board.job.model.entity.sample.JobDomain;
import com.board.job.model.entity.sample.LanguageLevel;
import com.board.job.model.entity.sample.WorkMode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Getter
@Builder
public class FullVacancyResponse {
    private long id;

    @JsonProperty(value = "looking_for")
    @NotBlank(message = "Here must be the candidate that you are looking for.")
    private String lookingFor;

    @NotNull
    @Enumerated(EnumType.STRING)
    private JobDomain domain;

    @JsonProperty(value = "description")
    @NotBlank(message = "Your vacancy description must contain detailed description of this.")
    private String detailDescription;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JsonProperty(value = "work_mode")
    private WorkMode workMode;

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

    @NotNull
    @Enumerated(EnumType.STRING)
    @JsonProperty(value = "english_level")
    private LanguageLevel englishLevel;

    @JsonProperty("posted_at")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime postedAt;
}
