package com.shau.mocap.fourier;

import com.shau.mocap.domain.Frame;
import com.shau.mocap.domain.Joint;
import com.shau.mocap.domain.request.Offset;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FourierPreProcessorTest {

    private List<Frame> frames;
    private FourierPreProcessor fourierPreProcessor;

    @Before
    public void initTests() {
        frames = new ArrayList<>();
        List<Joint> joints = new ArrayList<>();
        joints.add(new Joint(0, 0.0, 0.0, 0.0));
        joints.add(new Joint(1, 1.0, 2.0, 3.0));
        frames.add(new Frame(0, joints));
        List<Joint> joints2 = new ArrayList<>();
        joints2.add(new Joint(0, 4.0, 5.0, 6.0));
        joints2.add(new Joint(1, 7.0, 8.0, 9.0));
        frames.add(new Frame(1, joints2));
        fourierPreProcessor = new FourierPreProcessor();
    }

    @Test
    public void testOffsetPositionNoOffset() {
        Offset offset = new Offset();
        double[] newPosition = fourierPreProcessor.offsetPosition(frames.get(0), offset);
        assertThat(0.0, is(newPosition[0]));
        assertThat(0.0, is(newPosition[1]));
        assertThat(0.0, is(newPosition[2]));
    }

    @Test
    public void testOffsetPositionXyzOffset() {
        Offset offset = new Offset(null, 1.1, -2.2, 3.3);
        double[] newPosition = fourierPreProcessor.offsetPosition(frames.get(0), offset);
        assertThat(offset.getX(), is(newPosition[0]));
        assertThat(offset.getY(), is(newPosition[1]));
        assertThat(offset.getZ(), is(newPosition[2]));
    }

    @Test
    public void testOffsetPositionJointOffset() {
        Offset offset = new Offset(1, null, null, null);
        double[] newPosition = fourierPreProcessor.offsetPosition(frames.get(0), offset);
        assertThat(frames.get(0).getJoints().get(1).getX(), is(newPosition[0]));
        assertThat(frames.get(0).getJoints().get(1).getY(), is(newPosition[1]));
        assertThat(frames.get(0).getJoints().get(1).getZ(), is(newPosition[2]));
    }

    @Test
    public void testOffsetAndScaleAllJointsNoScalingNoOffset() {
        Offset offset = new Offset();
        double[] newPosition = fourierPreProcessor.offsetPosition(frames.get(0), offset);
        List<Joint> offsetJoints = fourierPreProcessor.offsetAndScaleAllJoints(frames.get(0), newPosition, 1.0);
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
        Offset offset = new Offset();
        double[] newPosition = fourierPreProcessor.offsetPosition(frames.get(0), offset);
        List<Joint> offsetJoints = fourierPreProcessor.offsetAndScaleAllJoints(frames.get(0), newPosition, scale);
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
        Offset offset = new Offset(null, 1.1, -2.3, 7.3);
        double[] newPosition = fourierPreProcessor.offsetPosition(frames.get(0), offset);
        List<Joint> offsetJoints = fourierPreProcessor.offsetAndScaleAllJoints(frames.get(0), newPosition, 1.0);
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
        Offset offset = new Offset(1, null, null, null);
        double[] newPosition = fourierPreProcessor.offsetPosition(frames.get(0), offset);
        List<Joint> offsetJoints = fourierPreProcessor.offsetAndScaleAllJoints(frames.get(0), newPosition, 1.0);
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
    public void testEaseJointsNoInterpolation() {
        Frame startFrame = frames.get(0);
        Frame currentFrame = frames.get(1);
        List<Joint> easedJoints = fourierPreProcessor.easeJoints(startFrame, currentFrame, 0.0);
        assertThat(2, is(easedJoints.size()));
        int i = 0;
        for (Joint joint : easedJoints) {
            assertThat(currentFrame.getJoints().get(i).getX(), is(joint.getX()));
            assertThat(currentFrame.getJoints().get(i).getY(), is(joint.getY()));
            assertThat(currentFrame.getJoints().get(i).getZ(), is(joint.getZ()));
            i++;
        }
    }

    @Test
    public void testEaseJointsFullInterpolation() {
        Frame startFrame = frames.get(0);
        Frame currentFrame = frames.get(1);
        List<Joint> easedJoints = fourierPreProcessor.easeJoints(startFrame, currentFrame, 1.0);
        assertThat(2, is(easedJoints.size()));
        int i = 0;
        for (Joint joint : easedJoints) {
            assertThat(startFrame.getJoints().get(i).getX(), is(joint.getX()));
            assertThat(startFrame.getJoints().get(i).getY(), is(joint.getY()));
            assertThat(startFrame.getJoints().get(i).getZ(), is(joint.getZ()));
            i++;
        }
    }
}