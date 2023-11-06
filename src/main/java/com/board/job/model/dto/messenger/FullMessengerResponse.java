package com.board.job.model.dto.messenger;

import com.board.job.model.dto.feedback.FeedbackResponse;
import com.board.job.model.entity.Vacancy;
import com.board.job.model.entity.candidate.CandidateProfile;
import com.board.job.model.entity.sample.Category;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Builder
public class FullMessengerResponse {
    private long id;

    @JsonProperty(value = "looking_for")
    @NotBlank(message = "Here must be the candidate that you are looking for.")
    private String lookingFor;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;

    private String companyName;

    @NotBlank(message = "You should declare country where candidate must be work.")
    private String country;

    @JsonProperty("posted_at")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime postedAt;

    @JsonProperty(value = "employer_name")
    @NotBlank(message = "The employer name cannot be blank")
    private String employerName;

    private Vacancy vacancy;

    private CandidateProfile candidateProfile;

    private List<FeedbackResponse> feedbacks;
}
