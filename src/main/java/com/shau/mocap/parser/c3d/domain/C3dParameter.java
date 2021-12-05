package com.shau.mocap.parser.c3d.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class C3dParameter {
    private String name;
    private String description;
    private int groupId;
    private boolean locked;
    private int dimensions;
    private int[] dimensionSizes;
    private List<Object> values;
}
