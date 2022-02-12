package com.shau.mocap.fourier.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FixedJoint {
    private int jointId;
    private Double xPos;
    private Double yPos;
    private Double zPos;
    private boolean xFixed;
    private boolean yFixed;
    private boolean zFixed;
}
