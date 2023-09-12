package com.board.job.model.mapper.candidate;

import com.board.job.model.dto.candidate_contact.CandidateContactRequest;
import com.board.job.model.dto.candidate_contact.CandidateContactResponse;
import com.board.job.model.entity.candidate.CandidateContact;
import org.mapstruct.Mapper;



@Mapper(componentModel = "spring")
public interface CandidateContactMapper {
    CandidateContactResponse getCandidateContactResponseFromCandidateContact(CandidateContact contact);

    CandidateContact getCandidateContactFromCandidateContactRequest(CandidateContactRequest request);
}
