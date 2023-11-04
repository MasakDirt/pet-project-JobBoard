package com.board.job.model.entity;

import com.board.job.model.entity.candidate.CandidateProfile;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "messengers_for_vacancies_reply")
public class MessengerForVacanciesReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonBackReference
    @JoinColumn(name = "vacancy_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Vacancy vacancy;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "candidate_id")
    private CandidateProfile candidateProfile;

    public MessengerForVacanciesReply() {
    }

    private MessengerForVacanciesReply(Vacancy vacancy, CandidateProfile candidate) {
        this.vacancy = vacancy;
        this.candidateProfile = candidate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessengerForVacanciesReply messengerForVacanciesReply = (MessengerForVacanciesReply) o;
        return id == messengerForVacanciesReply.id;
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

    public static MessengerForVacanciesReply of(Vacancy vacancy, CandidateProfile candidateProfile) {
        return new MessengerForVacanciesReply(vacancy, candidateProfile);
    }
}
