package com.board.job.service.mockito;

import com.board.job.model.entity.Image;
import com.board.job.model.entity.candidate.CandidateContact;
import com.board.job.model.entity.employer.EmployerProfile;
import com.board.job.repository.ImageRepository;
import com.board.job.service.ImageService;
import com.board.job.service.candidate.CandidateContactService;
import com.board.job.service.employer.EmployerProfileService;
import com.google.common.io.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class ImageServiceMockitoTests {
    @InjectMocks
    private ImageService imageService;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private CandidateContactService candidateContactService;
    @Mock
    private EmployerProfileService employerProfileService;

    @Test
    public void test_CreateWithCandidate() throws Exception {
        long candidateId = 3L;
        byte[] bytes = Files.toByteArray(new File("files/photos/donaldPicture.jpg"));
        Image expected = new Image();
        expected.setProfilePicture(bytes);
        expected.setContact(new CandidateContact());

        when(candidateContactService.readById(candidateId)).thenReturn(new CandidateContact());
        when(imageRepository.save(expected)).thenReturn(expected);
        Image actual = imageService.createWithCandidate(candidateId, bytes);

        verify(imageRepository, times(1)).save(expected);
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateWithEmployer() throws Exception {
        long employerId = 1L;
        byte[] bytes = Files.toByteArray(new File("files/photos/donaldPicture.jpg"));
        Image expected = new Image();
        expected.setProfilePicture(bytes);
        expected.setProfile(new EmployerProfile());

        when(employerProfileService.readById(employerId)).thenReturn(new EmployerProfile());
        when(imageRepository.save(expected)).thenReturn(expected);
        Image actual = imageService.createWithEmployer(employerId, bytes);

        verify(imageRepository, times(1)).save(expected);
        assertEquals(expected, actual);
    }

    @Test
    public void test_ReadById() {
        long id = 3L;
        Image image = new Image();

        when(imageRepository.findById(id)).thenReturn(Optional.of(image));
        Image actual = imageService.readById(id);

        verify(imageRepository, times(1)).findById(id);
        assertEquals(image, actual);
    }

    @Test
    public void test_getByIdCandidateImage() throws Exception{
        long id = 4L;
        Image image = new Image();
        image.setId(id);
        image.setProfilePicture(Files.toByteArray(new File("files/photos/donaldPicture.jpg")));
        image.setContact(new CandidateContact());

        when(imageRepository.findById(id)).thenReturn(Optional.of(image));
        imageService.getByIdCandidateImage(id);
    }

    @Test
    public void test_getByIdEmployerImage() throws Exception{
        long id = 4L;
        Image image = new Image();
        image.setId(id);
        image.setProfilePicture(Files.toByteArray(new File("files/photos/donaldPicture.jpg")));
        image.setProfile(new EmployerProfile());

        when(imageRepository.findById(id)).thenReturn(Optional.of(image));
        imageService.getByIdEmployerImage(id);
    }

    @Test
    public void test_Update() throws Exception {
        byte[] bytes = Files.toByteArray(new File("files/photos/donaldPicture.jpg"));
        Image image = new Image();
        image.setProfilePicture(bytes);

        when(imageRepository.findById(1L)).thenReturn(Optional.of(image));
        Image actual = imageService.update(1L, bytes);

        verify(imageRepository, times(1)).save(image);

        assertNotEquals(image, actual);
    }

    @Test
    public void test_Delete() {
        when(imageRepository.findById(5L)).thenReturn(Optional.of(new Image()));
        imageService.delete(5L);

        verify(imageRepository, times(1)).delete(new Image());
    }
}
