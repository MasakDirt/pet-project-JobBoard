package com.board.job.repository;

import com.board.job.model.entity.PDF_File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PDFRepository extends JpaRepository<PDF_File, Long> {
}
