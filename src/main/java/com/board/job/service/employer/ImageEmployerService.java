package com.board.job.service.employer;

import com.board.job.model.entity.employer.EmployerProfile;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.server.StreamResource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@AllArgsConstructor
public class ImageEmployerService {
    private final EmployerProfileService employerProfileService;

    public Image generateImage(EmployerProfile employerProfile) {
        var streamResource = new StreamResource(employerProfile.getEmployerName(), () -> {
            var attached = employerProfileService.getWithPropertyPictureAttachedById(employerProfile.getId());
            return new ByteArrayInputStream(attached.getProfilePicture());
        });
        streamResource.setContentType("image/png");
        var image = new Image(streamResource, "profile-picture");
        image.setHeight("100%");
        return image;
    }

    public void initUploadedImage(long id) {
        var memoryBuffer = new MultiFileMemoryBuffer();
        var upload = new Upload(memoryBuffer);
        upload.setAcceptedFileTypes("image/jpg", "image/jpeg", "image/png", "image/gif");

        upload.addSucceededListener(event -> {
            String attachedName = event.getFileName();
            try {
                BufferedImage inputImage = ImageIO.read(memoryBuffer.getInputStream(attachedName));
                ByteArrayOutputStream pngContent = new ByteArrayOutputStream();
                ImageIO.write(inputImage, "png", pngContent);
                employerProfileService.saveProfilePicture(id, pngContent.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
