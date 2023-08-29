package com.board.job.service.employer;

import com.board.job.model.entity.employer.EmployerProfile;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ImageEmployerServiceTests {
    @InjectMocks
    private final ImageEmployerService imageEmployerService;
    @Mock
    private final EmployerProfileService employerProfileService;

    @Autowired
    public ImageEmployerServiceTests(ImageEmployerService imageEmployerService, EmployerProfileService employerProfileService) {
        this.imageEmployerService = imageEmployerService;
        this.employerProfileService = employerProfileService;
    }

    @Test
    public void test_Injected_Components() {
        AssertionsForClassTypes.assertThat(imageEmployerService).isNotNull();
        AssertionsForClassTypes.assertThat(employerProfileService).isNotNull();
    }

    @Test
    void test_GenerateImage() {
        EmployerProfile employerProfile = new EmployerProfile();
        employerProfile.setId(1L);
        employerProfile.setEmployerName("Test Employer");
        when(employerProfileService.getWithPropertyPictureAttachedById(1L)).thenReturn(new byte[0]);

        Image image = imageEmployerService.generateImage(employerProfile);

        assertNotNull(image);
        assertEquals("profile-picture", image.getAlt().orElse(""));
        assertEquals("100%", image.getHeight());
    }

    @Test
    void test_InitUploadedImage() throws IOException {
        long id = 1L;
        MultiFileMemoryBuffer memoryBuffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(memoryBuffer);
        upload.setAcceptedFileTypes("image/jpg", "image/jpeg", "image/png", "image/gif");

        BufferedImage inputImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream pngContent = new ByteArrayOutputStream();
        ImageIO.write(inputImage, "png", pngContent);

        doNothing().when(employerProfileService).saveProfilePicture(id, pngContent.toByteArray());

        imageEmployerService.initUploadedImage(id);

        verify(employerProfileService, times(1)).saveProfilePicture(id, pngContent.toByteArray());
    }
}
