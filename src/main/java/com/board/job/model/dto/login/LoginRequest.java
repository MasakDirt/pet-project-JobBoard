package com.board.job.model.dto.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.codehaus.jackson.annotate.JsonProperty;

@Data
@Getter
@Builder
@NoArgsConstructor
public class LoginRequest {

    @NotNull(message = "The 'email' cannot be null!")
    @JsonProperty("e_mail")
    @Pattern(regexp = "[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}", message = "Must be a valid e-mail address")
    private String email;

    @NotBlank(message = "The 'password' cannot be blank!")
    private String password;

    private LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static LoginRequest of(String email, String password) {
        return new LoginRequest(email, password);
    }
}
