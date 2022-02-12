package com.shau.mocap.fourier.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FourierFrame {
    private Double fourierX1;
    private Double fourierX2;
    private Double fourierY1;
    private Double fourierY2;
    private Double fourierZ1;
    private Double fourierZ2;
}
