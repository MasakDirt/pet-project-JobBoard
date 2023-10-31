package com.board.job.model.dto.candidate_contact;

import com.board.job.model.entity.Image;
import com.board.job.model.entity.PDF_File;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateContactRequest {
    private long id;

    @JsonProperty(value = "candidate_name")
    private String candidateName;

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

    private Image image;

    private PDF_File pdf;
}
