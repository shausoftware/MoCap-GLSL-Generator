package com.shau.mocap.controller;

import com.shau.mocap.MocapApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@WebAppConfiguration
@ContextConfiguration(classes = { MocapApplication.class, FourierController.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class FourierFunctionalTest {

    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void testHiResFourierTransform() throws Exception {
        Path testInputPath = Paths.get("src","test", "resources", "fourier_hi_res.json");
        Path expectedResultPath = Paths.get("src","test", "resources", "fourier_hi_res.msh");
        String fourierRequest = Files.readString(testInputPath);
        String expected = Files.readString(expectedResultPath);

        MvcResult result = mockMvc.perform(put("/generateFourier")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(fourierRequest))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType("application/octet-stream"))
                        .andReturn();

        assertThat(result.getResponse().getContentAsString(), is(expected));
    }

    @Test
    public void testHiResFourierTransformScaleTooLargeWarning() throws Exception {
        Path testInputPath = Paths.get("src","test", "resources", "fourier_hi_res_scale_too_large_warning.json");
        Path expectedResultPath = Paths.get("src","test", "resources", "fourier_hi_res_scale_too_large_warning.msh");
        String fourierRequest = Files.readString(testInputPath);
        String expected = Files.readString(expectedResultPath);

        MvcResult result = mockMvc.perform(put("/generateFourier")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(fourierRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        assertThat(result.getResponse().getContentAsString(), is(expected));
    }

    @Test
    public void testLowResFourierTransform() throws Exception {
        Path testInputPath = Paths.get("src","test", "resources", "fourier_low_res.json");
        Path expectedResultPath = Paths.get("src","test", "resources", "fourier_low_res.msh");
        String fourierRequest = Files.readString(testInputPath);
        String expected = Files.readString(expectedResultPath);

        MvcResult result = mockMvc.perform(put("/generateFourier")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(fourierRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn();

        assertThat(result.getResponse().getContentAsString(), is(expected));
    }
}
