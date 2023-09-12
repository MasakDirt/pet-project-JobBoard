package com.board.job.model.dto.candidate_contact;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class CandidateContactResponse {
    private long id;

    @NotBlank(message = "The candidate name cannot be 'blank'")
    @JsonProperty(value = "candidate_name")
    private String candidateName;

    @NotNull
    @Pattern(regexp = "[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}", message = "Must be a valid e-mail address")
    @JsonProperty(value = "e_mail")
    private String email;

    @NotBlank(message = "You must write valid phone number.")
    private String phone;

    @NotNull
    @Pattern(regexp = "^@.*", message = "The telegram account must started with '@' character")
    private String telegram;

    @NotNull
    @JsonProperty(value = "linked_in")
    @Pattern(regexp = "^www\\.linkedin\\.co.*$",
            message = "Must be a valid LinkedIn link!")
    private String linkedInProfile;

    @NotNull
    @JsonProperty(value = "github")
    @Pattern(regexp = "^www\\.github\\.co.*$",
            message = "Must be a valid GitHub link!")
    private String githubUrl;

    @NotNull
    @Pattern(regexp = "^www\\.portfolio\\.co.*$",
            message = "Must be a valid portfolio link!")
    @JsonProperty(value = "portfolio_url")
    private String portfolioUrl;
}
