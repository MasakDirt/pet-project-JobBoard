package com.board.job.model.dto.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.codehaus.jackson.annotate.JsonProperty;

@Data
@Builder
@Getter
public class LoginRequest {

    @NotNull(message = "The 'email' cannot be null!")
    @JsonProperty("e_mail")
    @Pattern(regexp = "[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}", message = "Must be a valid e-mail address")
    private String email;

    @NotBlank(message = "The 'password' cannot be blank!")
    private String password;
}
