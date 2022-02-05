package com.shau.mocap.fourier;

import com.shau.mocap.domain.Frame;
import com.shau.mocap.domain.Joint;
import com.shau.mocap.domain.MoCapScene;
import com.shau.mocap.domain.request.Offset;
import com.shau.mocap.fourier.domain.FixedJoint;
import com.shau.mocap.fourier.utils.FourierJointFinder;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FourierPreProcessorTest {

    private MoCapScene moCapScene;
    private FourierPreProcessor fourierPreProcessor;

    @Before
    public void initTests() {
        List<Frame> frames = new ArrayList<>();
        List<Joint> frame1Joints = new ArrayList<>();
        frame1Joints.add(Joint.builder().id(0).x(0.0).y(0.0).z(0.0).build());
        frame1Joints.add(Joint.builder().id(1).x(1.0).y(2.0).z(3.0).build());
        frame1Joints.add(Joint.builder().id(2).x(-1.0).y(-2.0).z(-3.0).build());
        frames.add(new Frame(0, frame1Joints));
        List<Joint> frame2Joints = new ArrayList<>();
        frame2Joints.add(Joint.builder().id(0).x(0.0).y(0.0).z(0.0).build());
        frame2Joints.add(Joint.builder().id(1).x(1.13).y(2.13).z(3.13).build());
        frame2Joints.add(Joint.builder().id(2).x(-1.13).y(-2.13).z(-3.13).build());
        frames.add(new Frame(1, frame2Joints));
        moCapScene = MoCapScene.builder().filename("TEST").frames(frames).build();
        fourierPreProcessor = new FourierPreProcessor();
    }

    @Test
    public void testOffsetPositionNoOffset() {
        Offset offset = Offset.builder().build();
        double[] newPosition = fourierPreProcessor.offsetJointPositionsForFrame(moCapScene.getFrames().get(0), offset);

        assertThat(0.0, is(newPosition[0]));
        assertThat(0.0, is(newPosition[1]));
        assertThat(0.0, is(newPosition[2]));
    }

    @Test
    public void testOffsetPositionXyzOffset() {
        Offset offset = Offset.builder()
                .x(1.1)
                .y(-2.2)
                .z(3.3)
                .constrainX(true)
                .constrainY(true)
                .constrainZ(true)
                .build();
        double[] newPosition = fourierPreProcessor.offsetJointPositionsForFrame(moCapScene.getFrames().get(0), offset);

        assertThat(offset.getX(), is(newPosition[0]));
        assertThat(offset.getY(), is(newPosition[1]));
        assertThat(offset.getZ(), is(newPosition[2]));
    }

    @Test
    public void testOffsetPositionXyzOffsetNoConstraints() {
        Offset offset = Offset.builder()
                .x(1.1)
                .y(-2.2)
                .z(3.3)
                .build();
        double[] newPosition = fourierPreProcessor.offsetJointPositionsForFrame(moCapScene.getFrames().get(0), offset);

        assertThat(0.0, is(newPosition[0]));
        assertThat(0.0, is(newPosition[1]));
        assertThat(0.0, is(newPosition[2]));
    }

    @Test
    public void testOffsetPositionJointOffset() {
        Offset offset = Offset.builder()
                .jointId(1)
                .constrainX(true)
                .constrainY(true)
                .constrainZ(true)
                .build();
        List<Frame> frames = moCapScene.getFrames();
        double[] newPosition = fourierPreProcessor.offsetJointPositionsForFrame(frames.get(0), offset);

        assertThat(frames.get(0).getJoints().get(1).getX(), is(newPosition[0]));
        assertThat(frames.get(0).getJoints().get(1).getY(), is(newPosition[1]));
        assertThat(frames.get(0).getJoints().get(1).getZ(), is(newPosition[2]));
    }

    @Test
    public void testOffsetAndScaleAllJointsNoScalingNoOffset() {
        Offset offset = Offset.builder().build();
        List<Frame> frames = moCapScene.getFrames();
        double[] newPosition = fourierPreProcessor.offsetJointPositionsForFrame(frames.get(0), offset);
        List<Joint> offsetJoints = fourierPreProcessor.offsetAndScaleAllJointsForFrame(frames.get(0), newPosition, 1.0);
        assertThat(frames.get(0).getJoints().size(), is(offsetJoints.size()));
        for (int i = 0; i < frames.get(0).getJoints().size(); i++) {
            Joint testJoint = frames.get(0).getJoints().get(i);
            Joint offsetJoint = offsetJoints.get(i);

            assertThat(testJoint.getX(), is(offsetJoint.getX()));
            assertThat(testJoint.getY(), is(offsetJoint.getY()));
            assertThat(testJoint.getZ(), is(offsetJoint.getZ()));
        }
    }

    @Test
    public void testOffsetAndScaleAllJointWithScalingNoOffset() {
        double scale = 7.93;
        Offset offset = Offset.builder().build();
        List<Frame> frames = moCapScene.getFrames();
        double[] newPosition = fourierPreProcessor.offsetJointPositionsForFrame(frames.get(0), offset);
        List<Joint> offsetJoints = fourierPreProcessor.offsetAndScaleAllJointsForFrame(frames.get(0), newPosition, scale);
        assertThat(frames.get(0).getJoints().size(), is(offsetJoints.size()));
        for (int i = 0; i < frames.get(0).getJoints().size(); i++) {
            Joint testJoint = frames.get(0).getJoints().get(i);
            Joint offsetJoint = offsetJoints.get(i);

            assertThat(testJoint.getX() * scale, is(offsetJoint.getX()));
            assertThat(testJoint.getY() * scale, is(offsetJoint.getY()));
            assertThat(testJoint.getZ() * scale, is(offsetJoint.getZ()));
        }
    }

    @Test
    public void testOffsetAndScaleAllJointNoScalingXyzOffset() {
        Offset offset = Offset.builder()
                .x(1.1)
                .y(-2.3)
                .z(7.3)
                .constrainX(true)
                .constrainY(true)
                .constrainZ(true)
                .build();
        List<Frame> frames = moCapScene.getFrames();
        double[] newPosition = fourierPreProcessor.offsetJointPositionsForFrame(frames.get(0), offset);
        List<Joint> offsetJoints = fourierPreProcessor.offsetAndScaleAllJointsForFrame(frames.get(0), newPosition, 1.0);
        assertThat(frames.get(0).getJoints().size(), is(offsetJoints.size()));
        for (int i = 0; i < frames.get(0).getJoints().size(); i++) {
            Joint testJoint = frames.get(0).getJoints().get(i);
            Joint offsetJoint = offsetJoints.get(i);

            assertThat(testJoint.getX() - offset.getX(), is(offsetJoint.getX()));
            assertThat(testJoint.getY() - offset.getY(), is(offsetJoint.getY()));
            assertThat(testJoint.getZ() - offset.getZ(), is(offsetJoint.getZ()));
        }
    }

    @Test
    public void testOffsetAndScaleAllJointNoScalingJointOffset() {
        Offset offset = Offset.builder()
                .jointId(1)
                .constrainX(true)
                .constrainY(true)
                .constrainZ(true)
                .build();
        List<Frame> frames = moCapScene.getFrames();
        double[] newPosition = fourierPreProcessor.offsetJointPositionsForFrame(frames.get(0), offset);
        List<Joint> offsetJoints = fourierPreProcessor.offsetAndScaleAllJointsForFrame(frames.get(0), newPosition, 1.0);
        assertThat(frames.get(0).getJoints().size(), is(offsetJoints.size()));
        for (int i = 0; i < frames.get(0).getJoints().size(); i++) {
            Joint testJoint = frames.get(0).getJoints().get(i);
            Joint offsetJoint = offsetJoints.get(i);

            assertThat(testJoint.getX() - frames.get(0).getJoints().get(1).getX(), is(offsetJoint.getX()));
            assertThat(testJoint.getY() - frames.get(0).getJoints().get(1).getY(), is(offsetJoint.getY()));
            assertThat(testJoint.getZ() - frames.get(0).getJoints().get(1).getZ(), is(offsetJoint.getZ()));
        }
    }

    @Test
    public void testPreProcessSceneAndNoOffsetNoScaling() {
        Offset offset = Offset.builder().build();
        List<Frame> preProcessed = fourierPreProcessor.scaleAndOffsetScene(moCapScene, offset, 1.0);

        assertThat(moCapScene.getFrames().size(), is(preProcessed.size()));
        assertThat(0.0, is(preProcessed.get(0).getJoints().get(0).getX()));
        assertThat(2.0, is(preProcessed.get(0).getJoints().get(1).getY()));
        assertThat(-3.0, is(preProcessed.get(0).getJoints().get(2).getZ()));
        assertThat(0.0, is(preProcessed.get(1).getJoints().get(0).getX()));
        assertThat(2.13, is(preProcessed.get(1).getJoints().get(1).getY()));
        assertThat(-3.130, is(preProcessed.get(1).getJoints().get(2).getZ()));
    }

    @Test
    public void testPreProcessSceneAndNoOffsetWithScaling() {
        double scale = 3.1;
        Offset offset = Offset.builder().build();
        List<Frame> preProcessed = fourierPreProcessor.scaleAndOffsetScene(moCapScene, offset, scale);

        assertThat(moCapScene.getFrames().size(), is(preProcessed.size()));
        assertThat(0.0, is(preProcessed.get(0).getJoints().get(0).getX()));
        assertThat(scale * 2.0, is(preProcessed.get(0).getJoints().get(1).getY()));
        assertThat(scale * -3.0, is(preProcessed.get(0).getJoints().get(2).getZ()));
        assertThat(0.0, is(preProcessed.get(1).getJoints().get(0).getX()));
        assertThat(scale * 2.13, is(preProcessed.get(1).getJoints().get(1).getY()));
        assertThat(scale * -3.13, is(preProcessed.get(1).getJoints().get(2).getZ()));
    }

    @Test
    public void testPreProcessSceneAndXyzOffsetNoScaling() {
        Offset offset = Offset.builder()
                .x(1.0)
                .y(-1.0)
                .z(2.17)
                .constrainX(true)
                .constrainY(true)
                .constrainZ(true)
                .build();
        List<Frame> preProcessed = fourierPreProcessor.scaleAndOffsetScene(moCapScene, offset, 1.0);

        assertThat(moCapScene.getFrames().size(), is(preProcessed.size()));
        assertThat(0.0 - offset.getX(), is(preProcessed.get(0).getJoints().get(0).getX()));
        assertThat(2.0 - offset.getY(), is(preProcessed.get(0).getJoints().get(1).getY()));
        assertThat(-3.0 - offset.getZ(), is(preProcessed.get(0).getJoints().get(2).getZ()));
        assertThat(0.0 - offset.getX(), is(preProcessed.get(1).getJoints().get(0).getX()));
        assertThat(2.13 - offset.getY(), is(preProcessed.get(1).getJoints().get(1).getY()));
        assertThat(-3.13 - offset.getZ(), is(preProcessed.get(1).getJoints().get(2).getZ()));
    }

    @Test
    public void testPreProcessSceneAndJointOffsetNoScaling() {
        Offset offset = Offset.builder()
                .jointId(2)
                .constrainX(true)
                .constrainY(true)
                .constrainZ(true)
                .build();
        List<Frame> preProcessed = fourierPreProcessor.scaleAndOffsetScene(moCapScene, offset, 1.0);

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
    public void testPreProcessSceneAndJointOffsetWithScaling() {
        double scale =  7.13;
        Offset offset = Offset.builder()
                .jointId(2)
                .constrainX(true)
                .constrainY(true)
                .constrainZ(true)
                .build();
        List<Frame> preProcessed = fourierPreProcessor.scaleAndOffsetScene(moCapScene, offset, scale);

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
    public void testFindFixedJointFromJointList() {
        //Fixed joint found
        Optional<FixedJoint> fixedJoint = fourierPreProcessor.getFixedJoint(FourierJointFinder.findJointsByIndex(moCapScene, 0));

        assertThat(fixedJoint.isPresent(), is(true));
        assertThat(fixedJoint.get().isXFixed(), is(true));
        assertThat(fixedJoint.get().isYFixed(), is(true));
        assertThat(fixedJoint.get().isZFixed(), is(true));
        assertThat(fixedJoint.get().getXPos(), is(moCapScene.getFrames().get(0).getJoints().get(0).getX()));
        assertThat(fixedJoint.get().getYPos(), is(moCapScene.getFrames().get(0).getJoints().get(0).getY()));
        assertThat(fixedJoint.get().getZPos(), is(moCapScene.getFrames().get(0).getJoints().get(0).getZ()));

        //No fixed joint found
        fixedJoint = fourierPreProcessor.getFixedJoint(FourierJointFinder.findJointsByIndex(moCapScene, 1));
        assertThat(fixedJoint.isEmpty(), is(true));
    }

    @Test
    public void testFindFixedJointsFromScene() {
        Map<Integer, FixedJoint> fixedJoints = fourierPreProcessor.findFixedJoints(moCapScene);

        assertThat(fixedJoints.size(), is(1));
        FixedJoint fixedJoint = fixedJoints.get(0);
        assertThat(fixedJoint.getJointId(), is(0));
        assertThat(fixedJoint.isXFixed(), is(true));
        assertThat(fixedJoint.isYFixed(), is(true));
        assertThat(fixedJoint.isZFixed(), is(true));
        assertThat(fixedJoint.getXPos(), is(moCapScene.getFrames().get(0).getJoints().get(0).getX()));
        assertThat(fixedJoint.getYPos(), is(moCapScene.getFrames().get(0).getJoints().get(0).getY()));
        assertThat(fixedJoint.getZPos(), is(moCapScene.getFrames().get(0).getJoints().get(0).getZ()));
    }
}