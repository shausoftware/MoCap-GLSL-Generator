package com.shau.mocap.domain;

import java.io.Serializable;
import java.util.List;

public class Analysis implements Serializable {

    private String filename;
    private int numberOfFrames;
    private double minX;
    private double minY;
    private double minZ;
    private double maxX;
    private double maxY;
    private double maxZ;
    private List<AnalysedFrame> analysedFrames;

    public Analysis() {
    }

    public Analysis(String filename,
                    int numberOfFrames,
                    double minX,
                    double minY,
                    double minZ,
                    double maxX,
                    double maxY,
                    double maxZ,
                    List<AnalysedFrame> analysedFrames) {
        this.filename = filename;
        this.numberOfFrames = numberOfFrames;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.analysedFrames = analysedFrames;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getNumberOfFrames() {
        return numberOfFrames;
    }

    public void setNumberOfFrames(int numberOfFrames) {
        this.numberOfFrames = numberOfFrames;
    }

    public double getMinX() {
        return minX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public double getMinY() {
        return minY;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public double getMinZ() {
        return minZ;
    }

    public void setMinZ(double minZ) {
        this.minZ = minZ;
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public double getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(double maxZ) {
        this.maxZ = maxZ;
    }

    public List<AnalysedFrame> getAnalysedFrames() {
        return analysedFrames;
    }

    public void setAnalysedFrames(List<AnalysedFrame> analysedFrames) {
        this.analysedFrames = analysedFrames;
    }
}
