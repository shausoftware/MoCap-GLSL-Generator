package com.shau.mocap.service;

import com.shau.mocap.domain.Frame;
import com.shau.mocap.domain.MoCapScene;
import com.shau.mocap.parser.C3dParser;
import com.shau.mocap.parser.TrcParser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;

public class ParserServiceTest {

    private ParserService parserService;

    @Before
    public void initTests() {
        parserService = new ParserService();
        parserService.setC3dParser(new C3dParser());
        parserService.setTrcParser(new TrcParser());
    }

    @Test
    public void testParseTrcSuccess() throws Exception {
        Path testFilePath = Paths.get("src","test", "resources", "Dog_Run.trc");
        MoCapScene moCapScene = null;
        try {
            MultipartFile mpf = new MockMultipartFile("Dog_Run.trc",
                    "Dog_Run.trc",
                    "text/plain",
                    Files.readAllBytes(testFilePath));
            moCapScene = parserService.parse(mpf);
        } catch (Exception e) {
            fail("Should not fail parsing TRC");
        }
        assertThat(moCapScene.getFrames(), is(notNullValue()));
        assertThat(moCapScene.getFrames().size(), is(560));
    }

    @Test
    public void testParseC3dSuccess() throws Exception {
        Path testFilePath = Paths.get("src","test", "resources", "60_12.c3d");
        MoCapScene moCapScene = null;
        try {
            MultipartFile mpf = new MockMultipartFile("60_12.c3d",
                    "60_12.c3d",
                    "application/octet-stream",
                    Files.readAllBytes(testFilePath));
            moCapScene = parserService.parse(mpf);
        } catch (Exception e) {
            fail("Should not fail parsing C3D");
        }
        assertThat(moCapScene.getFrames(), is(notNullValue()));
        assertThat(moCapScene.getFrames().size(), is(1690));

        Frame frame = moCapScene.getFrames().get(0);
        assertThat(frame.getJoints().size(), is(82));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseWithInvalidFileType() throws Exception {
        Path testFilePath = Paths.get("src","test", "resources", "60_12.invalid");
        MultipartFile mpf = new MockMultipartFile("60_12.invalid",
                "60_12.invalid",
                "application/octet-stream",
                Files.readAllBytes(testFilePath));
        parserService.parse(mpf);
    }
}