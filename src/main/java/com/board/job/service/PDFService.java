package com.board.job.service;

import com.board.job.exception.InvalidFile;
import com.board.job.exception.UserHaveNoPDF;
import com.board.job.model.entity.PDF_File;
import com.board.job.repository.PDFRepository;
import com.board.job.service.candidate.CandidateContactService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

@Service
@AllArgsConstructor
public class PDFService {
    private final PDFRepository pdfRepository;
    private final CandidateContactService candidateContactService;

    public PDF_File create(long candidateId, byte[] fileBytes) {
        var pdf = new PDF_File();
        pdf.setContact(candidateContactService.readById(candidateId));
        pdf.setFileContent(fileBytes);

        return pdfRepository.save(pdf);
    }

    public PDF_File readById(long id) {
        return pdfRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("File not found"));
    }

    public String getBase64Pdf(long id) {
        return Base64.getEncoder().encodeToString(readById(id).getFileContent());
    }

    public PDF_File update(long id, byte[] fileBytes) {
        var old = readById(id);
        old.setFileContent(fileBytes);

        return pdfRepository.save(old);
    }

    public void delete(long id) {
        pdfRepository.delete(readById(id));
    }
}
