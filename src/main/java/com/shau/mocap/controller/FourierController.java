package com.shau.mocap.controller;

import com.shau.mocap.domain.request.FourierRequest;
import com.shau.mocap.service.FourierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.nio.charset.StandardCharsets;

@Controller
public class FourierController {

    private FourierService fourierService;
    @Autowired
    public void setFourierService(FourierService fourierService) {
        this.fourierService = fourierService;
    }

    @PutMapping("/generateFourier")
    public ResponseEntity<Resource> generateFourier(@RequestBody FourierRequest fourierRequest) {

        try {
            validateRequest(fourierRequest);
            String glslContent = fourierService.generateFourier(fourierRequest.getScene(),
                    fourierRequest.getStartFrame(),
                    fourierRequest.getEndFrame(),
                    fourierRequest.isUseEasing(),
                    fourierRequest.getEasingFrames(),
                    fourierRequest.getFourierScale(),
                    fourierRequest.getOffset(),
                    fourierRequest.getFourierFrames(),
                    fourierRequest.isUseLowRes(),
                    fourierRequest.getLowResStartFrame());
            byte[] content = glslContent.getBytes(StandardCharsets.UTF_8);
            ByteArrayResource resource = new ByteArrayResource(content);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(content.length)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateRequest(FourierRequest fourierRequest) {
        if (fourierRequest.getScene() == null)
            throw new IllegalArgumentException("Scene cannot be empty");
        if (fourierRequest.getStartFrame() < 0 || fourierRequest.getStartFrame() > fourierRequest.getEndFrame())
            throw new IllegalArgumentException("Invalid start frame");
        if (fourierRequest.getEndFrame() > fourierRequest.getScene().getFrames().size())
            throw new IllegalArgumentException("Invalid end frame");
        if (fourierRequest.getFourierFrames() < 0 || fourierRequest.getFourierFrames() > fourierRequest.getEndFrame())
            throw new IllegalArgumentException("Invalid fourier frames");
        if (fourierRequest.getFourierScale() <= 0.0)
            throw new IllegalArgumentException("Invalid fourier scale");
        if (fourierRequest.isUseEasing()) {
            if (fourierRequest.getEasingFrames() < 0 || fourierRequest.getEasingFrames() > (fourierRequest.getEndFrame() - fourierRequest.getStartFrame()))
                throw new IllegalArgumentException("Invalid easing frames");
        }
        if (fourierRequest.isUseLowRes()) {
            if (fourierRequest.getLowResStartFrame() < 0 || fourierRequest.getLowResStartFrame() > (fourierRequest.getEndFrame() - fourierRequest.getStartFrame()))
                throw new IllegalArgumentException("Invalid low res start frame");
            if ((fourierRequest.getEndFrame() - fourierRequest.getLowResStartFrame()) % 2 != 0)
                throw new IllegalArgumentException("Invalid low res start frame");
        }
    }
}
