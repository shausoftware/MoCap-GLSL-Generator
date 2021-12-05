package com.shau.mocap.parser.c3d.domain;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class C3dGroup {
    private int id;
    private String name;
    private String description;
    @Builder.Default
    private List<C3dParameter> parameters = new ArrayList<>();

    public void addParameter(C3dParameter parameter) {
        parameters.add(parameter);
    }
}
