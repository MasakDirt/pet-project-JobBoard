package com.board.job.model.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import static com.board.job.model.entity.User.NAME_REGEXP;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestWithPassword {
    @JsonProperty("first_name")
    @NotNull(message = "Write the first name!")
    @Pattern(regexp = NAME_REGEXP,
            message = "First name must start with a capital letter and followed by one or more lowercase")
    private String firstName;

    @JsonProperty("last_name")
    @NotNull(message = "Write the last name!")
    @Pattern(regexp = NAME_REGEXP,
            message = "Last name must start with a capital letter and followed by one or more lowercase")
    private String lastName;

    @JsonProperty("old_password")
    @NotBlank(message = "The password cannot be 'blank'")
    @Column(nullable = false)
    private String oldPassword;

    @JsonProperty("new_password")
    @NotBlank(message = "The password cannot be 'blank'")
    @Column(nullable = false)
    private String newPassword;
}
