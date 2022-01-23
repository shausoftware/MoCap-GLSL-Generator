package com.shau.mocap.domain.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Offset {
    private Integer jointId;
    private Double x;
    private Double y;
    private Double z;
    @Builder.Default
    private Boolean constrainX = false;
    @Builder.Default
    private Boolean constrainY = false;
    @Builder.Default
    private Boolean constrainZ = false;
}
