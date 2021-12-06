package com.shau.mocap.fourier;

import com.shau.mocap.domain.Frame;
import com.shau.mocap.domain.Joint;
import com.shau.mocap.domain.MoCapScene;
import com.shau.mocap.domain.request.Offset;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FourierTransformerTest {

    private FourierTransformer fourierTransformer;
    private MoCapScene moCapScene;

    @Before
    public void initTests() {
        fourierTransformer = new FourierTransformer();
        fourierTransformer.setFourierPreProcessor(new FourierPreProcessor());
        List<Frame> frames = new ArrayList<>();
        List<Joint> frame1Joints = new ArrayList<>();
        frame1Joints.add(new Joint(0, 0.0, 0.0, 0.0));
        frame1Joints.add(new Joint(1, 1.0, 2.0, 3.0));
        frame1Joints.add(new Joint(2, -1.0, -2.0, -3.0));
        frames.add(new Frame(0, frame1Joints));
        List<Joint> frame2Joints = new ArrayList<>();
        frame2Joints.add(new Joint(0, 0.0, 0.0, 0.0));
        frame2Joints.add(new Joint(1, 1.13, 2.13,3.13));
        frame2Joints.add(new Joint(2, -1.13, -2.13, -3.13));
        frames.add(new Frame(1, frame2Joints));
        moCapScene = new MoCapScene("TEST", frames);
    }

    @Test
    public void testPreProcessNoOffsetNoScaling() {
        Offset offset = new Offset();
        List<Frame> preProcessed = fourierTransformer.preProcess(moCapScene, offset, 1.0);
        assertThat(moCapScene.getFrames().size(), is(preProcessed.size()));
        assertThat(0.0, is(preProcessed.get(0).getJoints().get(0).getX()));
        assertThat(2.0, is(preProcessed.get(0).getJoints().get(1).getY()));
        assertThat(-3.0, is(preProcessed.get(0).getJoints().get(2).getZ()));
        assertThat(0.0, is(preProcessed.get(1).getJoints().get(0).getX()));
        assertThat(2.13, is(preProcessed.get(1).getJoints().get(1).getY()));
        assertThat(-3.130, is(preProcessed.get(1).getJoints().get(2).getZ()));
    }

    @Test
    public void testPreProcessNoOffsetWithScaling() {
        double scale = 3.1;
        Offset offset = new Offset();
        List<Frame> preProcessed = fourierTransformer.preProcess(moCapScene, offset, scale);
        assertThat(moCapScene.getFrames().size(), is(preProcessed.size()));
        assertThat(0.0, is(preProcessed.get(0).getJoints().get(0).getX()));
        assertThat(scale * 2.0, is(preProcessed.get(0).getJoints().get(1).getY()));
        assertThat(scale * -3.0, is(preProcessed.get(0).getJoints().get(2).getZ()));
        assertThat(0.0, is(preProcessed.get(1).getJoints().get(0).getX()));
        assertThat(scale * 2.13, is(preProcessed.get(1).getJoints().get(1).getY()));
        assertThat(scale * -3.13, is(preProcessed.get(1).getJoints().get(2).getZ()));
    }

    @Test
    public void testPreProcessXyzOffsetNoScaling() {
        Offset offset = new Offset(null, 1.0, -1.0, 2.17);
        List<Frame> preProcessed = fourierTransformer.preProcess(moCapScene, offset, 1.0);
        assertThat(moCapScene.getFrames().size(), is(preProcessed.size()));
        assertThat(0.0 - offset.getX(), is(preProcessed.get(0).getJoints().get(0).getX()));
        assertThat(2.0 - offset.getY(), is(preProcessed.get(0).getJoints().get(1).getY()));
        assertThat(-3.0 - offset.getZ(), is(preProcessed.get(0).getJoints().get(2).getZ()));
        assertThat(0.0 - offset.getX(), is(preProcessed.get(1).getJoints().get(0).getX()));
        assertThat(2.13 - offset.getY(), is(preProcessed.get(1).getJoints().get(1).getY()));
        assertThat(-3.13 - offset.getZ(), is(preProcessed.get(1).getJoints().get(2).getZ()));
    }

    @Test
    public void testPreProcessJointOffsetNoScaling() {
        Offset offset = new Offset(2, null, null, null);
        List<Frame> preProcessed = fourierTransformer.preProcess(moCapScene, offset, 1.0);
        assertThat(moCapScene.getFrames().size(), is(preProcessed.size()));
        assertThat(0.0 - moCapScene.getFrames().get(0).getJoints().get(offset.getJointId()).getX(),
                is(preProcessed.get(0).getJoints().get(0).getX()));
        assertThat(2.0 - moCapScene.getFrames().get(0).getJoints().get(offset.getJointId()).getY(),
                is(preProcessed.get(0).getJoints().get(1).getY()));
        assertThat(-3.0 - moCapScene.getFrames().get(0).getJoints().get(offset.getJointId()).getZ(),
                is(preProcessed.get(0).getJoints().get(2).getZ()));
        assertThat(0.0 - moCapScene.getFrames().get(1).getJoints().get(offset.getJointId()).getX(),
                is(preProcessed.get(1).getJoints().get(0).getX()));
        assertThat(2.13 - moCapScene.getFrames().get(1).getJoints().get(offset.getJointId()).getY(),
                is(preProcessed.get(1).getJoints().get(1).getY()));
        assertThat(-3.13 - moCapScene.getFrames().get(1).getJoints().get(offset.getJointId()).getZ(),
                is(preProcessed.get(1).getJoints().get(2).getZ()));
    }

    @Test
    public void testPreProcessJointOffsetWithScaling() {
        double scale =  7.13;
        Offset offset = new Offset(2, null, null, null);
        List<Frame> preProcessed = fourierTransformer.preProcess(moCapScene, offset, scale);
        assertThat(moCapScene.getFrames().size(), is(preProcessed.size()));
        assertThat(scale * (0.0 - moCapScene.getFrames().get(0).getJoints().get(offset.getJointId()).getX()),
                is(preProcessed.get(0).getJoints().get(0).getX()));
        assertThat(scale * (2.0 - moCapScene.getFrames().get(0).getJoints().get(offset.getJointId()).getY()),
                is(preProcessed.get(0).getJoints().get(1).getY()));
        assertThat(scale * (-3.0 - moCapScene.getFrames().get(0).getJoints().get(offset.getJointId()).getZ()),
                is(preProcessed.get(0).getJoints().get(2).getZ()));
        assertThat(scale * (0.0 - moCapScene.getFrames().get(1).getJoints().get(offset.getJointId()).getX()),
                is(preProcessed.get(1).getJoints().get(0).getX()));
        assertThat(scale * (2.13 - moCapScene.getFrames().get(1).getJoints().get(offset.getJointId()).getY()),
                is(preProcessed.get(1).getJoints().get(1).getY()));
        assertThat(scale * (-3.13 - moCapScene.getFrames().get(1).getJoints().get(offset.getJointId()).getZ()),
                is(preProcessed.get(1).getJoints().get(2).getZ()));
    }

    @Test
    public void testJointEasing() {
        List<Frame> easingFrames = fourierTransformer.easing(moCapScene.getFrames(), 0, 1, 1);
        assertThat(2, is(easingFrames.size()));
        //first frame joints not eased
        for (int i = 0; i < moCapScene.getFrames().get(0).getJoints().size(); i++) {
            assertThat(moCapScene.getFrames().get(0).getJoints().get(i).getX(), is(easingFrames.get(0).getJoints().get(i).getX()));
        }
        //second frame joints eased
        for (int i = 0; i < moCapScene.getFrames().get(0).getJoints().size(); i++) {
            assertThat(moCapScene.getFrames().get(0).getJoints().get(i).getX(), is(easingFrames.get(1).getJoints().get(i).getX()));
        }
    }

    @Test
    public void testTransform() {
        int fourierFrames = 2;
        Double[][] result = fourierTransformer.calculateFourier(moCapScene.getFrames().get(0).getJoints(), fourierFrames);
        assertThat(fourierFrames, is(result.length));
        result = fourierTransformer.calculateFourier(moCapScene.getFrames().get(1).getJoints(), fourierFrames);
        assertThat(fourierFrames, is(result.length));
    }
}