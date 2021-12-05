package com.shau.mocap.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Analysis {
    private String filename;
    private int numberOfFrames;
    private double minX;
    private double minY;
    private double minZ;
    private double maxX;
    private double maxY;
    private double maxZ;
    private List<AnalysedFrame> analysedFrames;
}
