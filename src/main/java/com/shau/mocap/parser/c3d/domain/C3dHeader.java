package com.shau.mocap.parser.c3d.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class C3dHeader {
    private int numberOfPoints;
    private int analogueMeasurementsPerFrame;
    private int rawFirstFrame;
    private int rawLastFrame;
    private int maxInterpolationGap;
    private float scale;
    private int dataStartBlock;
    private int analogueSamplesPerFrame;
    private float frameRate;
}
