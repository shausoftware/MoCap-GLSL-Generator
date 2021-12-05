package com.shau.mocap.domain;

import java.io.Serializable;
import java.util.List;

public class MoCapScene implements Serializable {

    private String originalFileName;
    private String filename;
    private List<Frame> frames;

    public MoCapScene() {
    }

    public MoCapScene(String fileName, List<Frame> frames) {
        this.filename  = fileName;
        originalFileName = fileName;
        this.frames = frames;
    }

    public Bounds getBounds() {
        return new Bounds(this);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<Frame> getFrames() {
        return frames;
    }

    public void setFrames(List<Frame> frames) {
        this.frames = frames;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
}
