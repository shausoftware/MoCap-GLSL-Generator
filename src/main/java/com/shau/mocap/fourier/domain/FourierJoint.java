package com.shau.mocap.fourier.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FourierJoint {
    private int jointId;
    private List<FourierFrame> fourierFrames;
}
