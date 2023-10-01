package com.board.job.controller.mockito;

import com.board.job.controller.ImageController;
import com.board.job.model.entity.Image;
import com.board.job.service.ImageService;
import com.google.common.io.Files;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class ImageControllerMockitoTests {
    @InjectMocks
    private ImageController controller;
    @Mock
    private ImageService service;
    @Mock
    private Authentication authentication;

    @Test
    public void test_GetByIdCandidateContactsImage() throws Exception {
        long ownerId = 5L;
        long candidateID = 4L;
        long id = 5L;

        when(service.getByIdCandidateImage(id)).thenReturn(new File("files/photos/helenPicture.jpg"));
        controller.getByIdCandidateContactsImage(ownerId, candidateID, id, authentication);

        verify(service, times(1)).getByIdCandidateImage(id);
    }

    @Test
    public void test_NotFound_GetByIdCandidateContactsImage() {
        long ownerId = 5L;
        long candidateID = 4L;
        long id = 100L;
        when(service.getByIdCandidateImage(id)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> controller.getByIdCandidateContactsImage(ownerId, candidateID, id, authentication));

        verify(service, times(1)).getByIdCandidateImage(id);
    }

    @Test
    public void test_GetByIdEmployerProfileImage() throws Exception {
        long ownerId = 10L;
        long employerId = 2L;
        long id = 1L;
        File file = new File("files/photos/helenPicture.jpg");

        when(service.getByIdEmployerImage(id)).thenReturn(file);
        controller.getByIdEmployerProfileImage(ownerId, employerId, id, authentication);

        verify(service, times(1)).getByIdEmployerImage(id);
    }

    @Test
    public void test_NotFound_GetByIdEmployerProfileImage() {
        long ownerId = 10L;
        long employerId = 2L;
        long id = 0L;
        when(service.getByIdEmployerImage(id)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> controller.getByIdEmployerProfileImage(ownerId, employerId, id, authentication));

        verify(service, times(1)).getByIdEmployerImage(id);
    }

    @Test
    public void test_AddCandidatePhoto() throws Exception {
        long ownerId = 3L;
        long candidateId = 34L;
        String filename = "files/photos/donaldPicture.jpg";
        byte[] bytes = Files.toByteArray(new File(filename));

        Image image = new Image();
        image.setProfilePicture(bytes);

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                filename,
                MediaType.TEXT_PLAIN_VALUE,
                bytes
        );

        when(service.createWithCandidate(candidateId, bytes)).thenReturn(image);
        controller.addCandidatePhoto(ownerId, candidateId, multipartFile, authentication);

        verify(service, times(1)).createWithCandidate(candidateId, bytes);
    }

    @Test
    public void test_AddEmployerPhoto() throws Exception {
        long ownerId = 3L;
        long employerId = 34L;
        String filename = "files/photos/donaldPicture.jpg";
        byte[] bytes = Files.toByteArray(new File(filename));

        Image image = new Image();
        image.setProfilePicture(bytes);

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                filename,
                MediaType.TEXT_PLAIN_VALUE,
                bytes
        );

        when(service.createWithEmployer(employerId, multipartFile.getBytes())).thenReturn(image);
        controller.addEmployerPhoto(ownerId, employerId, multipartFile, authentication);

        verify(service, times(1)).createWithEmployer(employerId, multipartFile.getBytes());
    }

    @Test
    public void test_UpdateCandidatePhoto() throws Exception {
        long ownerId = 4L;
        long candidateId = 3L;
        long id = 34L;
        String filename = "files/photos/donaldPicture.jpg";
        byte[] bytes = Files.toByteArray(new File(filename));

        Image image = new Image();
        image.setProfilePicture(bytes);

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                filename,
                MediaType.TEXT_PLAIN_VALUE,
                bytes
        );

        when(service.update(id, bytes)).thenReturn(image);
        controller.updateCandidatePhoto(ownerId, candidateId, id, multipartFile, authentication);

        verify(service, times(1)).update(id, bytes);
    }

    @Test
    public void test_UpdateEmployerPhoto() throws Exception {
        long ownerId = 4L;
        long employerID = 3L;
        long id = 34L;
        String filename = "files/photos/donaldPicture.jpg";
        byte[] bytes = Files.toByteArray(new File(filename));

        Image image = new Image();
        image.setProfilePicture(bytes);

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                filename,
                MediaType.TEXT_PLAIN_VALUE,
                bytes
        );

        when(service.update(id, bytes)).thenReturn(image);
        controller.updateEmployerPhoto(ownerId, employerID, id, multipartFile, authentication);

        verify(service, times(1)).update(id, bytes);
    }

    @Test
    public void test_DeleteCandidatePhoto() {
        long ownerId = 4L;
        long candidateId = 3L;
        long id = 3L;
        controller.deleteCandidatePhoto(ownerId, candidateId, id, authentication);

        verify(service, times(1)).delete(id);
    }

    @Test
    public void test_DeleteEmployerPhoto() {
        long ownerId = 4L;
        long employerID = 3L;
        long id = 3L;
        controller.deleteEmployerPhoto(ownerId, employerID, id, authentication);

        verify(service, times(1)).delete(id);
    }
}
