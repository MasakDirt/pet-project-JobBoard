package com.board.job.repository;

import com.board.job.model.entity.Messenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessengerRepository  extends JpaRepository<Messenger, Long> {
    List<Messenger> findAllByCandidateId(long candidateId);

    List<Messenger> findAllByVacancyId(long vacancyId);
}
