package com.shau.mocap.fourier;

import com.shau.mocap.domain.Frame;
import com.shau.mocap.domain.Joint;
import com.shau.mocap.domain.MoCapScene;
import com.shau.mocap.domain.request.Offset;
import com.shau.mocap.fourier.domain.FixedJoint;
import com.shau.mocap.fourier.utils.FourierJointFinder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class FourierPreProcessor implements FourierConstants {

    public List<Frame> scaleAndOffsetScene(MoCapScene moCapScene, Offset offset, double scale) {
        List<Frame> processFrames = new ArrayList<>();
        for (Frame frame : moCapScene.getFrames()) {
            double[] offsetPosition = offsetJointPositionsForFrame(frame, offset);
            List<Joint> processJoints = offsetAndScaleAllJointsForFrame(frame, offsetPosition, scale);
            processFrames.add(new Frame(frame.getId(), processJoints));
        }
        return processFrames;
    }

    public double[] offsetJointPositionsForFrame(Frame frame, Offset offset) {
        double[] offsetPosition = new double[] {0.0, 0.0, 0.0};
        if (offset.getJointId() != null) {
            Joint centerJoint = frame.getJoints().stream()
                    .filter(joint -> joint.getId() == offset.getJointId())
                    .findFirst()
                    .orElse(null);
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

    public List<Joint> offsetAndScaleAllJointsForFrame(Frame frame, double[] offsetPosition, double scale) {
        return frame.getJoints().stream().map(j ->
                new Joint(j.getId(),
                        (j.getX() - offsetPosition[0]) * scale,
                        (j.getY() - offsetPosition[1]) * scale,
                        (j.getZ() - offsetPosition[2]) * scale,
                        j.getColour(),
                        j.isDisplay())
        ).collect(Collectors.toList());
    }

    public Map<Integer, FixedJoint> findFixedJoints(List<Frame> frames) {
        Map<Integer, FixedJoint> fixedJoints = new HashMap<>();
        int nJoints = frames.get(0).getJoints().size();
        for (int i = 0; i < nJoints; i++) {
            int index = i;
            Optional<FixedJoint> fixedJoint = getFixedJoint(FourierJointFinder.findJointsByIndex(frames, index));
            if (fixedJoint.isPresent()) {
                fixedJoints.put(fixedJoint.get().getJointId(), fixedJoint.get());
            }
        }
        return fixedJoints;
    }

    public Optional<FixedJoint> getFixedJoint(List<Joint> joints) {
        Optional<FixedJoint> fixedJoint = Optional.empty();
        Set<Double> xp = new HashSet<>();
        Set<Double> yp = new HashSet<>();
        Set<Double> zp = new HashSet<>();
        joints.stream().forEach(j -> {
            xp.add(j.getX());
            yp.add(j.getY());
            zp.add(j.getZ());
        });
        boolean fixX = xp.size() == 1;
        boolean fixY = yp.size() == 1;
        boolean fixZ = zp.size() == 1;
        if (fixX || fixY || fixZ) {
            Joint joint = joints.get(0);
            fixedJoint = Optional.of(FixedJoint.builder()
                    .jointId(joint.getId())
                    .xFixed(fixX)
                    .xPos(fixX ? joint.getX() : null)
                    .yFixed(fixY)
                    .yPos(fixY ? joint.getY() : null)
                    .zFixed(fixZ)
                    .zPos(fixZ ? joint.getZ() : null)
                    .build());
        }
        return fixedJoint;
    }

    //TODO: easing is currently linear
    /*
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
     */
}
