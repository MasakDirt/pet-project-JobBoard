package com.board.job.service.authorization;

import com.board.job.model.entity.PDF_File;
import com.board.job.service.PDFService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthPDFService {
    private final PDFService pdfService;
    private final AuthCandidateContactService authCandidateContactService;

    public boolean isUsersSameByIdAndUserOwnerCandidateContactsAndCandidateContactsContainPDF(
            long userId, long candidateContactId, long id, String authEmail) {

        return authCandidateContactService
                .isUsersSameByIdAndUserOwnerCandidateContacts(userId, candidateContactId, authEmail)
                && getPDF(id).getContact().getId() == candidateContactId;
    }

    public PDF_File getPDF(long id) {
        return pdfService.readById(id);
    }
}
