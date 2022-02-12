package com.shau.mocap.fourier;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LowResBounds {
    private int[] bounds;
    private int maxLowResDevX;
    private int maxLowResDevY;
    private int maxLowResDevZ;
    private float lowResScaleEncodeX;
    private float lowResScaleDecodeX;
    private float lowResScaleEncodeY;
    private float lowResScaleDecodeY;
    private float lowResScaleEncodeZ;
    private float lowResScaleDecodeZ;
}
