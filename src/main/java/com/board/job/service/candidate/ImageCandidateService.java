package com.board.job.service.candidate;

import com.board.job.model.entity.candidate.CandidateContacts;
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
public class ImageCandidateService {
    private final CandidateContactsService candidateContactsService;

    public Image generateImage(CandidateContacts candidateContacts) {
        var streamResource = new StreamResource(candidateContacts.getCandidateName(), () -> {
            var attached = candidateContactsService.getWithPropertyPictureAttachedById(candidateContacts.getId());
            return new ByteArrayInputStream(attached);
        });
        streamResource.setContentType("image/png");
        Image image = new Image(streamResource, "profile-picture");
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
                candidateContactsService.saveProfilePicture(id, pngContent.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
