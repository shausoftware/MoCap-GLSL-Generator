package com.shau.mocap.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Frame {
    private int id;
    private List<Joint> joints;

    public Bounds getBounds() {
        return new Bounds(this);
    }
}
