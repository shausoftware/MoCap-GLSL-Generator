package com.shau.mocap.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Joint {
    private int id;
    private Double x;
    private Double y;
    private Double z;
    @Builder.Default
    private String colour = "white";
    @Builder.Default
    private boolean display = true;
}
