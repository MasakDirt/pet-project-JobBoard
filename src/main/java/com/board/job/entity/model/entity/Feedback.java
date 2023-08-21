package com.board.job.entity.model.entity;

import com.board.job.entity.model.entity.candidate.CandidateProfile;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Table
@Entity
@Getter
@Setter
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @NotBlank(message = "You should write text about why you the best candidate for this vacation.")
    private String text;

    @JsonBackReference
    @JoinColumn(name = "vacancy_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Vacancy vacancy;

    @JsonBackReference
    @JoinColumn(name = "candidate_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private CandidateProfile candidateProfile;
}
