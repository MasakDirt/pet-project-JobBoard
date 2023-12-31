package com.board.job.model.entity.candidate;

import com.board.job.model.entity.Image;
import com.board.job.model.entity.PDF_File;
import com.board.job.model.entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;


@Entity
@Getter
@Setter
@Table(name = "candidate_contacts")
public class CandidateContact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "The candidate name cannot be 'blank'")
    @Column(name = "candidate_name")
    private String candidateName;

    @NotNull
    @Pattern(regexp = "[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}", message = "Must be a valid e-mail address")
    @Column(name = "e_mail", unique = true, nullable = false)
    private String email;

    @NotBlank(message = "You must write valid phone number.")
    private String phone;

    @NotNull
    @Pattern(regexp = "^@.*", message = "The telegram account must started with '@' character")
    private String telegram;

    @NotNull
    @Column(name = "linked_in", nullable = false)
    @Pattern(regexp = "^www\\.linkedin\\.co.*$",
            message = "Must be a valid LinkedIn link!")
    private String linkedInProfile;

    @NotNull
    @Column(name = "github", nullable = false)
    @Pattern(regexp = "^www\\.github\\.co.*$",
            message = "Must be a valid GitHub link!")
    private String githubUrl;

    @NotNull
    @Pattern(regexp = "^www\\.portfolio\\.co.*$",
            message = "Must be a valid portfolio link!")
    @Column(name = "portfolio_url", nullable = false)
    private String portfolioUrl;

    @JsonManagedReference
    @OneToOne(mappedBy = "contact", cascade = CascadeType.ALL)
    private Image image;

    @JsonManagedReference
    @OneToOne(mappedBy = "contact", cascade = CascadeType.ALL)
    private PDF_File pdf;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "owner_id")
    private User owner;

    public CandidateContact() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CandidateContact that = (CandidateContact) o;
        return id == that.id && Objects.equals(candidateName, that.candidateName) && Objects.equals(email, that.email) &&
                Objects.equals(phone, that.phone) && Objects.equals(telegram, that.telegram) &&
                Objects.equals(linkedInProfile, that.linkedInProfile) && Objects.equals(githubUrl, that.githubUrl)
                && Objects.equals(portfolioUrl, that.portfolioUrl) && Objects.equals(pdf, that.pdf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, candidateName, email, phone, telegram, linkedInProfile, githubUrl, portfolioUrl, pdf);
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
                ", gitHubUrl='" + githubUrl + '\'' +
                ", portfolioUrl='" + portfolioUrl + '\'' +
                '}';
    }

    public void setCandidateName(String userWriteName, String ownerName) {
        if (userWriteName == null || userWriteName.trim().isEmpty())
            this.candidateName = ownerName;
    }

    public void setEmail(String userWriteEmail, String ownerEmail) {
        if (userWriteEmail == null || userWriteEmail.trim().isEmpty())
            this.email = ownerEmail;
    }
}
