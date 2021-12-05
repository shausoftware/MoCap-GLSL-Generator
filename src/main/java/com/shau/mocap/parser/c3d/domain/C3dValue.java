package com.shau.mocap.parser.c3d.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class C3dValue {
    private List<C3dSpatialPoint> spatialPoints;
    private List<Float> analogValues;
}
