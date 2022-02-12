package com.shau.mocap.fourier;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FourierBounds {
    private int[] bounds;
    private int xOffs;
    private int yOffs;
    private int zOffs;
}
