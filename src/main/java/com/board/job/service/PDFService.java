package com.board.job.service;

import com.board.job.model.entity.PDF_File;
import com.board.job.repository.PDFRepository;
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

    public PDF_File create(String filename) {
        return pdfRepository.save(setFileContent(filename));
    }

    public PDF_File readById(long id) {
        return pdfRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("File not found"));
    }

    public PDF_File update(long id, String newFile) {
        var update = readById(id);

        update.setFileContent(setFileContent(newFile).getFileContent());
        return pdfRepository.save(update);
    }

    public void delete(long id) {
        pdfRepository.delete(readById(id));
    }

    private PDF_File setFileContent(String filename){
        var pdfFile = new PDF_File();
        try {
            pdfFile.setFileContent(Files.readAllBytes(Path.of(filename)));
        } catch (IOException io) {
            throw new IllegalArgumentException(String.format("File with name %s not found", filename));
        }
        return pdfFile;
    }
}
