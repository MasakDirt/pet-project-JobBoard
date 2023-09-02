package com.board.job.model.entity;

import com.board.job.model.entity.candidate.CandidateContact;
import com.board.job.model.entity.employer.EmployerProfile;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "profile_picture", columnDefinition = "LONGBLOB")
    private byte[] profilePicture;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "candidate_contact_id")
    private CandidateContact contact;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "employer_profile_id")
    private EmployerProfile profile;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return id == image.id && Arrays.equals(profilePicture, image.profilePicture) && Objects.equals(contact, image.contact);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, contact);
        result = 31 * result + Arrays.hashCode(profilePicture);
        return result;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", profilePicture=" + Arrays.toString(profilePicture) +
                ", contact=" + contact +
                '}';
    }
}
