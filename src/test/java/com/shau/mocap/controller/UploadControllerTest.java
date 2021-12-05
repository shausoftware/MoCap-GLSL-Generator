package com.shau.mocap.controller;

import com.shau.mocap.MocapApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebAppConfiguration
@ContextConfiguration(classes = { MocapApplication.class, UploadController.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class UploadControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void testImportSuccessNo() throws Exception {
        Path testFilePath = Paths.get("src","test", "resources", "60_12.c3d");
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "60_12.c3d",
                "application/octet-stream",
                Files.readAllBytes(testFilePath)
        );

        mockMvc.perform(multipart("/import").file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.filename").value("60_12.c3d"));
    }

    @Test
    public void testImportInvalidFile() throws Exception {
        Path testFilePath = Paths.get("src","test", "resources", "60_12.invalid");
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "60_12.invalid",
                "application/octet-stream",
                Files.readAllBytes(testFilePath)
        );

        mockMvc.perform(multipart("/import").file(file))
                .andExpect(status().is4xxClientError());
    }
}