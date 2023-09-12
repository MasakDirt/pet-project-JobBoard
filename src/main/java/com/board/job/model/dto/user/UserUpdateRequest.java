package com.board.job.model.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import static com.board.job.model.entity.User.NAME_REGEXP;

@Data
@Getter
@Builder
public class UserUpdateRequest {
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
}
