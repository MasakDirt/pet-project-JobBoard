package com.board.job.model.entity;

import com.board.job.model.entity.candidate.CandidateContacts;
import com.board.job.model.entity.employer.EmployerProfile;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    @Column(name = "file", nullable = false)
    private String fileName;

    @JsonManagedReference
    @OneToOne(mappedBy = "photo", cascade = CascadeType.ALL)
    private EmployerProfile employerProfile;

    @JsonManagedReference
    @OneToOne(mappedBy = "photo", cascade = CascadeType.ALL)
    private CandidateContacts candidateContacts;

    public Image(){}

    public Image(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return id == image.id && Objects.equals(fileName, image.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileName);
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
