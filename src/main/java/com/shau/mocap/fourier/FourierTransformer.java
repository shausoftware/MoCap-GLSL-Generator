package com.shau.mocap.fourier;

import com.shau.mocap.domain.Frame;
import com.shau.mocap.domain.Joint;
import com.shau.mocap.fourier.domain.FourierFrame;
import com.shau.mocap.fourier.domain.FourierJoint;
import com.shau.mocap.fourier.domain.FourierTransform;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FourierTransformer implements FourierConstants {

    public FourierTransform createTransform(List<Frame> processFrames,
                                           int startFrame,
                                           int endFrame,
                                           int fourierFrames) {
        List<FourierJoint> fourierJointList = new ArrayList<>();
        for (int i = 0;  i < processFrames.get(startFrame).getJoints().size(); i++) {
            int index = i;
            List<Joint> fourierJoints = processFrames.stream()
                    .skip(startFrame)
                    .limit(endFrame - startFrame)
                    .map(f -> f.getJoints().get(index))
                    .collect(Collectors.toList());

            if (fourierJoints.get(0).isDisplay()) {
                fourierJointList.add(calculateFourierJoint(fourierJoints, fourierFrames));
            }
        }
        return FourierTransform.builder().fourierJoints(fourierJointList).build();
    }

    public FourierJoint calculateFourierJoint(List<Joint> joints, int fourierFrames) {
        List<FourierFrame> fourierFrameList = new ArrayList<>();
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
            fourierFrameList.add(FourierFrame.builder()
                            .fourierX1(fc[0])
                            .fourierX2(fc[1])
                            .fourierY1(fc[2])
                            .fourierY2(fc[3])
                            .fourierZ1(fc[4])
                            .fourierZ2(fc[5])
                    .build());
        }
        return FourierJoint.builder()
                .jointId(joints.get(0).getId())
                .fourierFrames(fourierFrameList)
                .build();
    }

    public LowResBounds lowResBounds(FourierTransform fourierTransform,
                                     int cutoff,
                                     FourierBounds fourierBounds) {

        int[] bounds = fourierBounds(fourierTransform,
                                     cutoff,
                                     fourierBounds.getXOffs(),
                                     fourierBounds.getYOffs(),
                                     fourierBounds.getZOffs());

        //maximum deviation in fourier data required for low resolution data
        int maxLowResDevX = bounds[MAXX] - bounds[MINX];
        int maxLowResDevY = bounds[MAXY] - bounds[MINY];
        int maxLowResDevZ = bounds[MAXZ] - bounds[MINZ];

        return LowResBounds.builder()
                .bounds(bounds)
                .maxLowResDevX(maxLowResDevX)
                .maxLowResDevY(maxLowResDevY)
                .maxLowResDevZ(maxLowResDevZ)
                .lowResScaleEncodeX(maxLowResDevX < 255 ? 1.0f : 255.0f / (float) maxLowResDevX)
                .lowResScaleDecodeX(maxLowResDevX < 255 ? 1.0f : (float) maxLowResDevX / 255.0f)
                .lowResScaleEncodeY(maxLowResDevY < 255 ? 1.0f : 255.0f / (float) maxLowResDevY)
                .lowResScaleDecodeY(maxLowResDevY < 255 ? 1.0f : (float) maxLowResDevY / 255.0f)
                .lowResScaleEncodeZ(maxLowResDevZ < 255 ? 1.0f : 255.0f / (float) maxLowResDevZ)
                .lowResScaleDecodeZ(maxLowResDevZ < 255 ? 1.0f : (float) maxLowResDevZ / 255.0f)
                .build();
    }

    public FourierBounds fourierBounds(FourierTransform fourierTransform) {
        int[] bounds = fourierBounds(fourierTransform, 0, 0, 0, 0);
        return FourierBounds.builder()
                .bounds(bounds)
                .xOffs(bounds[MINX] < 0 ? Math.abs(bounds[MINX]) : 0)
                .yOffs(bounds[MINY] < 0 ? Math.abs(bounds[MINY]) : 0)
                .zOffs(bounds[MINZ] < 0 ? Math.abs(bounds[MINZ]) : 0).build();
    }

    private int[] fourierBounds(FourierTransform fourierTransform, int cutoff, int xOffs, int yOffs, int zOffs) {
        int[] bounds = {Integer.MAX_VALUE, -Integer.MAX_VALUE, Integer.MAX_VALUE, -Integer.MAX_VALUE, Integer.MAX_VALUE, -Integer.MAX_VALUE};
        fourierTransform.getFourierJoints().stream()
                .skip(cutoff)
                .flatMap(fj -> fj.getFourierFrames().stream()).forEach(f -> {
                    bounds[0] = lower(bounds[0], f.getFourierX1() + xOffs);
                    bounds[0] = lower(bounds[0], f.getFourierX2() + xOffs);
                    bounds[1] = higher(bounds[1], f.getFourierX1() + xOffs);
                    bounds[1] = higher(bounds[1], f.getFourierX2() + xOffs);
                    bounds[2] = lower(bounds[2], f.getFourierY1() + yOffs);
                    bounds[2] = lower(bounds[2], f.getFourierY2() + yOffs);
                    bounds[3] = higher(bounds[3], f.getFourierY1() + yOffs);
                    bounds[3] = higher(bounds[3], f.getFourierY2() + yOffs);
                    bounds[4] = lower(bounds[4], f.getFourierZ1() + zOffs);
                    bounds[4] = lower(bounds[4], f.getFourierZ2() + zOffs);
                    bounds[5] = higher(bounds[5], f.getFourierZ1() + zOffs);
                    bounds[5] = higher(bounds[5], f.getFourierZ2() + zOffs);
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
