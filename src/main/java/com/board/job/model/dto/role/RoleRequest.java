package com.board.job.model.dto.role;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {
    @NotNull
    @Pattern(regexp = "^[A-Z._-]+$", message = "The role name must be in upper case!")
    private String name;
}
