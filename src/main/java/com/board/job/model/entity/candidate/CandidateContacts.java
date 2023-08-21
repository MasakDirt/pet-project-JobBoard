package com.board.job.model.entity.candidate;

import com.board.job.model.entity.Image;
import com.board.job.model.entity.PDF_File;
import com.board.job.model.entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;


@Table
@Entity
@Getter
@Setter
public class CandidateContacts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "The candidate name cannot be blank")
    @Column(name = "candidate_name")
    private String candidateName;

    @NotNull
    @Pattern(regexp = "[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}", message = "Must be a valid e-mail address")
    @Column(name = "e-mail", unique = true, nullable = false)
    private String email;

    @NotNull
    @Pattern(regexp = "^(\\\\+\\\\d{2})?\\\\(?\\\\d{3}\\\\)?-?\\\\d{3}-?\\\\d{2}-?\\\\d{2}$",
            message = "Phone number must contain numbers and look like - 068xxx90xx or +38(068)-xxx-90-xx")
    private String phone;

    @NotNull
    @Pattern(regexp = "^@.*", message = "The telegram account must started with '@' character")
    private String telegram;

    @NotNull
    @Column(name = "linked_in", nullable = false)
    private String linkedInProfile;

    @NotNull
    private String gitHubUrl;

    @NotNull
    private String portfolioUrl;

    @NotNull
    @JsonBackReference
    @JoinColumn(name = "pdf_id")
    @OneToOne(fetch = FetchType.EAGER)
    private PDF_File pdf;

    @NotNull
    @JsonBackReference
    @JoinColumn(name = "photo_id")
    @OneToOne(fetch = FetchType.EAGER)
    private Image photo;

    @NotNull
    @JsonBackReference
    @JoinColumn(name = "owner_id")
    @OneToOne(fetch = FetchType.EAGER)
    private User owner;

    public CandidateContacts() {
        candidateName = owner.getFirstName() + owner.getLastName();
        email = owner.getEmail();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CandidateContacts that = (CandidateContacts) o;
        return id == that.id && Objects.equals(candidateName, that.candidateName) && Objects.equals(email, that.email) &&
                Objects.equals(phone, that.phone) && Objects.equals(telegram, that.telegram) &&
                Objects.equals(linkedInProfile, that.linkedInProfile) && Objects.equals(gitHubUrl, that.gitHubUrl)
                && Objects.equals(portfolioUrl, that.portfolioUrl) && Objects.equals(pdf, that.pdf) && Objects.equals(photo, that.photo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, candidateName, email, phone, telegram, linkedInProfile, gitHubUrl, portfolioUrl, pdf, photo);
    }

    @Override
    public String toString() {
        return "CandidateContacts{" +
                "id=" + id +
                ", candidateName='" + candidateName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", telegram='" + telegram + '\'' +
                ", linkedInProfile='" + linkedInProfile + '\'' +
                ", gitHubUrl='" + gitHubUrl + '\'' +
                ", portfolioUrl='" + portfolioUrl + '\'' +
                ", pdf=" + pdf +
                ", photo=" + photo +
                '}';
    }
}
