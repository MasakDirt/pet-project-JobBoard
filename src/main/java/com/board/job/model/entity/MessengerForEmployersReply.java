package com.board.job.model.entity;

import com.board.job.model.entity.candidate.CandidateProfile;
import com.board.job.model.entity.employer.EmployerProfile;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "messengers_for_employers_reply")
public class MessengerForEmployersReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonBackReference
    @JoinColumn(name = "employer_profile_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private EmployerProfile employerProfile;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "candidate_profile_id")
    private CandidateProfile candidateProfile;

    public MessengerForEmployersReply() {
    }

    private MessengerForEmployersReply(EmployerProfile employerProfile, CandidateProfile candidate) {
        this.employerProfile = employerProfile;
        this.candidateProfile = candidate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessengerForEmployersReply messenger = (MessengerForEmployersReply) o;
        return id == messenger.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Messenger{" +
                "id=" + id +
                '}';
    }

    public static MessengerForEmployersReply of(EmployerProfile employerProfile, CandidateProfile candidateProfile) {
        return new MessengerForEmployersReply(employerProfile, candidateProfile);
    }
}
