package com.shau.mocap.domain.request;

import com.shau.mocap.domain.MoCapScene;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FourierRequest {
    private int fourierFrames;
    private boolean useLowRes;
    private int lowResStartFrame;
    private int startFrame;
    private int endFrame;
    private boolean useEasing;
    private int easingFrames;
    private double fourierScale;
    private MoCapScene scene;
    private Offset offset;
}
