package com.board.job.model.dto.role;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class RoleResponse {
    private long id;

    @NotNull
    @Pattern(regexp = "^[A-Z._-]+$", message = "The role name must be in upper case!")
    @Column(unique = true, nullable = false)
    private String name;
}
