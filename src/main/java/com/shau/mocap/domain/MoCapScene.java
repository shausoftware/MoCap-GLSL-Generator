package com.shau.mocap.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class MoCapScene {
    private String originalFileName;
    private String filename;
    private List<Frame> frames;

    public Bounds getBounds() {
        return new Bounds(this);
    }
}
