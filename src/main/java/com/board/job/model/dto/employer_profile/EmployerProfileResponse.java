package com.board.job.model.dto.employer_profile;

import com.board.job.model.entity.Image;
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
public class EmployerProfileResponse {
    private long id;

    @JsonProperty(value = "employer_name")
    @NotBlank(message = "The employer name cannot be blank")
    private String employerName;

    @JsonProperty(value = "position")
    @NotBlank(message = "You must write your position")
    private String positionInCompany;

    @JsonProperty(value = "company")
    @NotBlank(message = "You should write your company name")
    private String companyName;

    @NotNull
    @Pattern(regexp = "^@.*", message = "The telegram account must started with '@' character")
    private String telegram;

    @NotBlank(message = "You must write valid phone number.")
    private String phone;

    @NotNull
    @JsonProperty(value = "linked_in")
    @Pattern(regexp = "^www\\.linkedin\\.co.*$",
            message = "Must be a valid LinkedIn link!")
    private String linkedInProfile;

    private Image image;
}
