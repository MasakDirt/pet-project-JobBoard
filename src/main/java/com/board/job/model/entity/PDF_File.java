package com.board.job.model.entity;

import com.board.job.model.entity.candidate.CandidateContacts;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Objects;

@Table
@Entity
@Getter
@Setter
public class PDF_File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "content", nullable = false)
    private byte[] fileContent;

    @JsonManagedReference
    @OneToOne(mappedBy = "pdf", cascade = CascadeType.ALL)
    private CandidateContacts candidateContacts;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PDF_File pdfFile = (PDF_File) o;
        return Objects.equals(id, pdfFile.id) && Arrays.equals(fileContent, pdfFile.fileContent);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id);
        result = 31 * result + Arrays.hashCode(fileContent);
        return result;
    }

    @Override
    public String toString() {
        return "PDF_File{" +
                "id=" + id +
                ", fileContent=" + Arrays.toString(fileContent) +
                ", candidateContacts=" + candidateContacts +
                '}';
    }
}
