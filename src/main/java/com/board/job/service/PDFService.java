package com.board.job.service;

import com.board.job.model.entity.PDF_File;
import com.board.job.repository.PDFRepository;
import com.board.job.service.candidate.CandidateContactService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@AllArgsConstructor
public class PDFService {
    private final PDFRepository pdfRepository;
    private final CandidateContactService candidateContactService;

    public PDF_File create(long candidateId, String filename) {
        var pdf = new PDF_File();
        pdf.setContact(candidateContactService.readById(candidateId));
        if (filename != null) {
            pdf.setFileContent(setContent(filename));
        }

        return pdfRepository.save(pdf);
    }

    public PDF_File readById(long id) {
        return pdfRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("File not found"));
    }

    public PDF_File update(long id, String newFile) {
        var update = readById(id);

        update.setFileContent(setContent(newFile));
        return pdfRepository.save(update);
    }

    public void delete(long id) {
        pdfRepository.delete(readById(id));
    }

    private byte[] setContent(String filename){
        try {
            return Files.readAllBytes(Path.of(filename));
        } catch (IOException io) {
            throw new IllegalArgumentException(String.format("File with name %s not found", filename));
        }
    }
}
