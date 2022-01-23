package com.shau.mocap.fourier;

import com.shau.mocap.domain.Frame;
import com.shau.mocap.domain.Joint;
import com.shau.mocap.domain.request.Offset;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FourierPreProcessor {

    public double[] offsetPosition(Frame frame, Offset offset) {
        double[] offsetPosition = new double[] {0.0, 0.0, 0.0};
        if (offset.getJointId() != null) {
            Joint centerJoint = frame.getJoints().get(offset.getJointId());
            offsetPosition = new double[] {offset.getConstrainX() ? centerJoint.getX() : 0.0,
                    offset.getConstrainY() ? centerJoint.getY() : 0.0,
                    offset.getConstrainZ() ? centerJoint.getZ() : 0.0};
        } else if (offset.getX() != null && offset.getY() != null && offset.getZ() != null){
            offsetPosition = new double[] {offset.getConstrainX() ? offset.getX() : 0.0,
                    offset.getConstrainY() ? offset.getY() : 0.0,
                    offset.getConstrainZ() ? offset.getZ() : 0.0};
        }
        return offsetPosition;
    }

    public List<Joint> offsetAndScaleAllJoints(Frame frame, double[] offsetPosition, double scale) {
        return frame.getJoints().stream().map(j ->
                new Joint(j.getId(),
                        (j.getX() - offsetPosition[0]) * scale,
                        (j.getY() - offsetPosition[1]) * scale,
                        (j.getZ() - offsetPosition[2]) * scale,
                        j.getColour(),
                        j.isDisplay())
        ).collect(Collectors.toList());
    }

    //TODO: easing is cuurrently linear
    public List<Joint> easeJoints(Frame startFrame, Frame currentFrame, double dx) {
        List<Joint> easedJoints =  new ArrayList<>();
        for (int i = 0; i < currentFrame.getJoints().size(); i++) {
            Joint startJoint = startFrame.getJoints().get(i);
            Joint currentJoint = currentFrame.getJoints().get(i);
            easedJoints.add(new Joint(currentJoint.getId(),
                    currentJoint.getX() + (startJoint.getX() - currentJoint.getX()) * dx,
                    currentJoint.getY() + (startJoint.getY() - currentJoint.getY()) * dx,
                    currentJoint.getZ() + (startJoint.getZ() - currentJoint.getZ()) * dx,
                    currentJoint.getColour(),
                    currentJoint.isDisplay()));
        }
        return easedJoints;
    }

    public int[] fourierBounds(Double[][][] transform, int cutoff, int xOffs, int yOffs, int zOffs) {
        int[] bounds = {Integer.MAX_VALUE, -Integer.MAX_VALUE, Integer.MAX_VALUE, -Integer.MAX_VALUE, Integer.MAX_VALUE, -Integer.MAX_VALUE};
        Arrays.stream(transform).flatMap(j -> Arrays.stream(j).skip(cutoff)).forEach((Double[] f) -> {
            bounds[0] = lower(bounds[0], f[0] + xOffs);
            bounds[0] = lower(bounds[0], f[1] + xOffs);
            bounds[1] = higher(bounds[1], f[0] + xOffs);
            bounds[1] = higher(bounds[1], f[1] + xOffs);
            bounds[2] = lower(bounds[2], f[2] + yOffs);
            bounds[2] = lower(bounds[2], f[3] + yOffs);
            bounds[3] = higher(bounds[3], f[2] + yOffs);
            bounds[3] = higher(bounds[3], f[3] + yOffs);
            bounds[4] = lower(bounds[4], f[4] + zOffs);
            bounds[4] = lower(bounds[4], f[5] + zOffs);
            bounds[5] = higher(bounds[5], f[4] + zOffs);
            bounds[5] = higher(bounds[5], f[5] + zOffs);
        });
        return bounds;
    }

    private int higher(int cMax, Double val) {
        if (val > cMax)
            return val.intValue();
        return cMax;
    }

    private int lower(int cMin, Double val) {
        if (val < cMin)
            return val.intValue();
        return cMin;
    }
}
