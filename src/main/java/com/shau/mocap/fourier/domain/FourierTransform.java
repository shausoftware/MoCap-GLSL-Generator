package com.shau.mocap.fourier.domain;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class FourierTransform {
    private List<FourierJoint> fourierJoints;
}
