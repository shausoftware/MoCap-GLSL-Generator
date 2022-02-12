package com.shau.mocap.fourier;

import com.shau.mocap.domain.Frame;
import com.shau.mocap.domain.Joint;
import com.shau.mocap.domain.MoCapScene;
import com.shau.mocap.fourier.domain.FourierFrame;
import com.shau.mocap.fourier.domain.FourierJoint;
import com.shau.mocap.fourier.domain.FourierTransform;
import com.shau.mocap.fourier.utils.FourierJointFinder;
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
    }

    @Test
    public void testTransformOfJoints() {
        int fourierFrames = 2;
        List<Joint> joints = FourierJointFinder.findJointsByIndex(moCapScene.getFrames(), 0);
        FourierJoint fourierJoint = fourierTransformer.calculateFourierJoint(joints, fourierFrames);

        assertThat(fourierJoint.getFourierFrames().size(), is(fourierFrames));

        joints = FourierJointFinder.findJointsByIndex(moCapScene.getFrames(), 2);
        fourierJoint = fourierTransformer.calculateFourierJoint(joints, fourierFrames);

        assertThat(fourierJoint.getFourierFrames().size(), is(fourierFrames));
    }

    @Test
    public void testTransformOfFrames() {
        int fourierFrames = 2;
        FourierTransform fourierTransform = fourierTransformer.createTransform(moCapScene.getFrames(),
                0,
                2,
                fourierFrames);

        assertThat(fourierTransform.getFourierJoints().isEmpty(), is(false));
        assertThat(fourierTransform.getFourierJoints().size(), is(3));
        for (FourierJoint fourierJoint : fourierTransform.getFourierJoints()) {
            assertThat(fourierJoint.getFourierFrames().size(), is(fourierFrames));
        }
    }

    @Test
    public void testBoundsHighRes() {
        FourierTransform testTransform = createTestTransform();
        FourierBounds bounds = fourierTransformer.fourierBounds(testTransform);

        testHighResBounds(bounds);
    }

    @Test
    public void testLowResBounds() {
        FourierTransform testTransform = createTestTransform();
        FourierBounds bounds = fourierTransformer.fourierBounds(testTransform);

        testHighResBounds(bounds);

        LowResBounds lowResBounds = fourierTransformer.lowResBounds(testTransform, 1, bounds);

        assertThat(lowResBounds.getBounds()[0], is(0));
        assertThat(lowResBounds.getBounds()[1], is(6));
        assertThat(lowResBounds.getBounds()[2], is(0));
        assertThat(lowResBounds.getBounds()[3], is(6));
        assertThat(lowResBounds.getBounds()[4], is(0));
        assertThat(lowResBounds.getBounds()[5], is(6));
        assertThat(lowResBounds.getLowResScaleDecodeX(), is(1.0f));
        assertThat(lowResBounds.getLowResScaleDecodeY(), is(1.0f));
        assertThat(lowResBounds.getLowResScaleDecodeZ(), is(1.0f));
        assertThat(lowResBounds.getLowResScaleEncodeX(), is(1.0f));
        assertThat(lowResBounds.getLowResScaleEncodeY(), is(1.0f));
        assertThat(lowResBounds.getLowResScaleEncodeZ(), is(1.0f));
        assertThat(lowResBounds.getMaxLowResDevX(), is(6));
        assertThat(lowResBounds.getMaxLowResDevY(), is(6));
        assertThat(lowResBounds.getMaxLowResDevZ(), is(6));
    }

    private void testHighResBounds(FourierBounds bounds) {
        assertThat(bounds.getBounds()[0], is(-2));
        assertThat(bounds.getBounds()[1], is(4));
        assertThat(bounds.getBounds()[2], is(-2));
        assertThat(bounds.getBounds()[3], is(4));
        assertThat(bounds.getBounds()[4], is(-2));
        assertThat(bounds.getBounds()[5], is(4));
        assertThat(bounds.getXOffs(), is(2));
        assertThat(bounds.getYOffs(), is(2));
        assertThat(bounds.getZOffs(), is(2));
    }

    private FourierTransform createTestTransform() {
        List<FourierJoint> fourierJoints = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            List<FourierFrame> fourierFrames = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                double v = j%2 == 0 ? i*j : i*j*-1.0;
                fourierFrames.add(FourierFrame.builder()
                        .fourierX1(v)
                        .fourierX2(v)
                        .fourierY1(v)
                        .fourierY2(v)
                        .fourierZ1(v)
                        .fourierZ2(v)
                        .build());
            }
            fourierJoints.add(FourierJoint.builder()
                    .jointId(i)
                    .fourierFrames(fourierFrames)
                    .build());
        }
        return FourierTransform.builder().fourierJoints(fourierJoints).build();
    }
}