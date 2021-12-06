package com.shau.mocap.fourier;

import com.shau.mocap.domain.Frame;
import com.shau.mocap.domain.Joint;
import com.shau.mocap.domain.request.Offset;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FourierPreProcessor {

    public double[] offsetPosition(Frame frame, Offset offset) {
        double[] offsetPosition = new double[] {0.0, 0.0, 0.0};
        if (offset.getJointId() != null) {
            Joint centerJoint = frame.getJoints().get(offset.getJointId());
            offsetPosition = new double[] {centerJoint.getX(), centerJoint.getY(), centerJoint.getZ()};
        } else if (offset.getX() != null && offset.getY() != null && offset.getZ() != null){
            offsetPosition = new double[] {offset.getX(), offset.getY(), offset.getZ()};
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
}
