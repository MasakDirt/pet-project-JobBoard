package com.board.job.model.dto.feedback;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Getter
@Builder
public class FeedbackResponse {
    private String id;

    @JsonProperty("owner_id")
    private long ownerId;

    @NotBlank(message = "You should write text about why you the best candidate for this vacation.")
    private String text;

    @JsonProperty(value = "send_at")
    private LocalDateTime sendAt;
}
