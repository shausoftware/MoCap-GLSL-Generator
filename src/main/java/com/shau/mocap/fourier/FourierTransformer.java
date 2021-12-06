package com.shau.mocap.fourier;

import com.shau.mocap.domain.Frame;
import com.shau.mocap.domain.Joint;
import com.shau.mocap.domain.MoCapScene;
import com.shau.mocap.domain.request.Offset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FourierTransformer {

    private FourierPreProcessor fourierPreProcessor;
    @Autowired
    public void setFourierPreProcessor(FourierPreProcessor fourierPreProcessor) {
        this.fourierPreProcessor =  fourierPreProcessor;
    }

    public List<Frame> preProcess(MoCapScene moCapScene, Offset offset, double scale) {
        List<Frame> processFrames = new ArrayList<>();
        for (Frame frame : moCapScene.getFrames()) {
            double[] offsetPosition = fourierPreProcessor.offsetPosition(frame, offset);
            List<Joint> processJoints = fourierPreProcessor.offsetAndScaleAllJoints(frame, offsetPosition, scale);
            processFrames.add(new Frame(frame.getId(), processJoints));
        }
        return processFrames;
    }

    public List<Frame> easing(List<Frame> frames, int start, int end, int easingFrames) {
        List<Frame> easedFrames = new ArrayList<>();
        Frame startFrame = frames.get(start);
        for (int i = 0; i < frames.size(); i++) {
            Frame currentFrame = frames.get(i);
            if (i >= end - easingFrames) {
                double dx = 1.0 - ((end - i) / easingFrames);
                easedFrames.add(new Frame(currentFrame.getId(), fourierPreProcessor.easeJoints(startFrame, currentFrame, dx)));
            } else {
                easedFrames.add(currentFrame);
            }
        }
        return easedFrames;
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
