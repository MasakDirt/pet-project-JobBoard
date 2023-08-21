package com.board.job.model.entity;

import com.board.job.model.entity.candidate.CandidateContacts;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @Column(name = "file", nullable = false)
    @NotBlank(message = "The file name cannot be 'blank', please write it!")
    private String filename;

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
        return Objects.equals(id, pdfFile.id) && Objects.equals(filename, pdfFile.filename) && Arrays.equals(fileContent, pdfFile.fileContent);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, filename);
        result = 31 * result + Arrays.hashCode(fileContent);
        return result;
    }

    @Override
    public String toString() {
        return "PDF_File{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", fileContent=" + Arrays.toString(fileContent) +
                ", candidateContacts=" + candidateContacts +
                '}';
    }
}
