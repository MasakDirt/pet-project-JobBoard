package com.board.job.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "feedbacks")
public class Feedback {
    @Id
    private String id;

    @Column(nullable = false)
    @NotBlank(message = "You should write text about why you the best candidate for this vacation.")
    private String text;

    private long vacancyId;

    private long candidateProfileId;
}
