package com.shau.mocap.fourier;

import com.shau.mocap.domain.Frame;
import com.shau.mocap.domain.Joint;
import com.shau.mocap.domain.MoCapScene;
import com.shau.mocap.domain.request.Offset;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FourierTransformer {

    public List<Frame> preProcess(MoCapScene moCapScene, Offset offset, double scale) {
        List<Frame> processFrames = new ArrayList<>();
        for (Frame frame : moCapScene.getFrames()) {
            double offsetX = 0.0;
            double offsetY = 0.0;
            double offsetZ = 0.0;
            if (offset.getJointId() != null) {
                Joint centerJoint = frame.getJoints().get(offset.getJointId());
                offsetX = centerJoint.getX();
                offsetY = centerJoint.getY();
                offsetZ = centerJoint.getZ();
            } else if (offset.getX() != null && offset.getY() != null && offset.getZ() != null){
                offsetX = offset.getX();
                offsetY = offset.getY();
                offsetZ = offset.getZ();
            }

            List<Joint> processJoints = new ArrayList<>();
            for (Joint joint : frame.getJoints()) {
                Joint processJoint = new Joint(joint.getId(),
                        (joint.getX() - offsetX) * scale,
                        (joint.getY() - offsetY) * scale,
                        (joint.getZ() - offsetZ) * scale);
                processJoint.setColour(joint.getColour());
                processJoint.setDisplay(joint.isDisplay());
                processJoints.add(processJoint);
            }
            processFrames.add(new Frame(frame.getId(), processJoints));
        }
        return processFrames;
    }

    public Double[][] calculateFourier(List<Joint> joints, int fourierFrames) {

        Double[][] fcs = new Double[fourierFrames][6];
        int frames = joints.size();
        for (int k = 0;  k < fourierFrames; k++) {
            Double[] fc = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
            for (int i = 0; i < frames; i++) {
                Joint joint = joints.get(i);
                Double xPos = joint.getX();
                Double yPos = joint.getY();
                Double zPos = joint.getZ();

                double an = -6.283185 * k * i / frames;
                Double[] ex = new Double[] {Math.cos(an), Math.sin(an)};
                fc[0] += xPos * ex[0];
                fc[1] += xPos * ex[1];
                fc[2] += yPos * ex[0];
                fc[3] += yPos * ex[1];
                fc[4] += zPos * ex[0];
                fc[5] += zPos * ex[1];
            }
            fcs[k] = fc;
        }
        return fcs;
    }
}
