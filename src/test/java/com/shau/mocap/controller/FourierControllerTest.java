package com.shau.mocap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.shau.mocap.MocapApplication;
import com.shau.mocap.domain.Frame;
import com.shau.mocap.domain.Joint;
import com.shau.mocap.domain.MoCapScene;
import com.shau.mocap.domain.request.FourierRequest;
import com.shau.mocap.domain.request.Offset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@ContextConfiguration(classes = { MocapApplication.class, FourierController.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class FourierControllerTest {

    private static final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private FourierRequest request;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

        List<Frame> frames = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            List<Joint> joints = new ArrayList<>();
            joints.add(Joint.builder().id(0).x(0.0).y(0.0).z(0.0).build());
            frames.add(new Frame(i, joints));
        }

        request = FourierRequest.builder().build();
        request.setScene(MoCapScene.builder().filename("testfile.mcp").frames(frames).build());
        request.setOffset(Offset.builder().jointId(0).x(0.0).y(0.0).z(0.0).build());
        request.setStartFrame(0);
        request.setEndFrame(frames.size());
        request.setFourierFrames(frames.size());
        request.setFourierScale(1.0);
        request.setUseEasing(true);
        request.setEasingFrames(2);
        request.setUseLowRes(true);
        request.setLowResStartFrame(8);
    }

    @Test
    public void testGenerateFourierSuccess() throws Exception {
        mockMvc.perform(put("/generateFourier")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(ow.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"));
    }

    @Test
    public void testGenerateFourierNoScene() throws Exception {
        request.setScene(null);
        mockMvc.perform(put("/generateFourier")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(ow.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGenerateFourierNegativeStartFrame() throws Exception {
        request.setStartFrame(-1);
        mockMvc.perform(put("/generateFourier")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(ow.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGenerateFourierStartFrameGreaterThanEndFrame() throws Exception {
        request.setStartFrame(request.getScene().getFrames().size() + 1);
        mockMvc.perform(put("/generateFourier")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(ow.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGenerateFourierEndFrameGreaterThanTotalFrames() throws Exception {
        request.setEndFrame(request.getScene().getFrames().size() + 1);
        mockMvc.perform(put("/generateFourier")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(ow.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGenerateFourierNegativeFourierFrames() throws Exception {
        request.setFourierFrames(-1);
        mockMvc.perform(put("/generateFourier")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(ow.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGenerateFourierFourierFramesGreaterThanEndFrame() throws Exception {
        request.setFourierFrames(request.getEndFrame() + 1);
        mockMvc.perform(put("/generateFourier")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(ow.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGenerateFourierNegativeFourierScale() throws Exception {
        request.setFourierScale(-1.0);
        mockMvc.perform(put("/generateFourier")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(ow.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGenerateFourierZeroFourierScale() throws Exception {
        request.setFourierScale(0.0);
        mockMvc.perform(put("/generateFourier")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(ow.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGenerateFourierNegativeEasingFrames() throws Exception {
        request.setEasingFrames(-1);
        mockMvc.perform(put("/generateFourier")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(ow.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGenerateFourierEasingFramesGreaterThanStartEndFrameRange() throws Exception {
        request.setStartFrame(2);
        request.setEasingFrames(request.getEndFrame() - request.getStartFrame() + 1);
        mockMvc.perform(put("/generateFourier")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(ow.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGenerateFourierNegativeLowResStartFrame() throws Exception {
        request.setLowResStartFrame(-1);
        mockMvc.perform(put("/generateFourier")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(ow.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGenerateFourierLowResStartFrameGreaterThanStartEndFrameRange() throws Exception {
        request.setStartFrame(2);
        request.setLowResStartFrame(request.getEndFrame() - request.getStartFrame() + 1);
        mockMvc.perform(put("/generateFourier")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(ow.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGenerateFourierLowResStartFrameEndFrameRangeNotMod2() throws Exception {
        request.setLowResStartFrame(request.getEndFrame() - 3);
        mockMvc.perform(put("/generateFourier")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(ow.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}