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
import org.springframework.web.bind.annotation.RestController;

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
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
