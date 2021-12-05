package com.shau.mocap.controller;

import com.shau.mocap.domain.MoCapScene;
import com.shau.mocap.exception.ParserException;
import com.shau.mocap.service.ParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class UploadController {

    private ParserService parserService;
    @Autowired
    public void setParserService(ParserService parserService) {
        this.parserService = parserService;
    }

    @PostMapping("/import")
    public ResponseEntity<MoCapScene> importMoCap(@RequestParam("file") MultipartFile file) {

        try {
            MoCapScene result = parserService.parse(file);
            return ResponseEntity.ok(result);
        } catch (IOException | ParserException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
