package com.board.job.model.entity;

import com.board.job.model.entity.candidate.CandidateContacts;
import com.board.job.model.entity.employer.EmployerProfile;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Table
@Entity
@Getter
@Setter
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "The file name cannot be 'blank', please write it!")
    @Column(name = "file", nullable = false)
    private String filename;

    @JsonManagedReference
    @OneToOne(mappedBy = "photo", cascade = CascadeType.ALL)
    private EmployerProfile employerProfile;

    @JsonManagedReference
    @OneToOne(mappedBy = "photo", cascade = CascadeType.ALL)
    private CandidateContacts candidateContacts;

    public Image(){}

    public Image(String filename) {
        this.filename = filename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return id == image.id && Objects.equals(filename, image.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, filename);
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", fileName='" + filename + '\'' +
                '}';
    }
}
