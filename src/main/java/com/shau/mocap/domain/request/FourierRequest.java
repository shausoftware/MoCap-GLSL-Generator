package com.shau.mocap.domain.request;

import com.shau.mocap.domain.MoCapScene;

import java.io.Serializable;

public class FourierRequest implements Serializable {

    private int fourierFrames;
    private boolean useLowRes;
    private int lowResStartFrame;
    private int startFrame;
    private int endFrame;
    private double fourierScale;
    private MoCapScene scene;
    private Offset offset;

    public int getFourierFrames() {
        return fourierFrames;
    }

    public void setFourierFrames(int fourierFrames) {
        this.fourierFrames = fourierFrames;
    }
    
    public boolean isUseLowRes() {
        return useLowRes;
    }

    public void setUseLowRes(boolean useLowRes) {
        this.useLowRes = useLowRes;
    }

    public int getLowResStartFrame() {
        return lowResStartFrame;
    }

    public void setLowResStartFrame(int lowResStartFrame) {
        this.lowResStartFrame = lowResStartFrame;
    }

    public int getStartFrame() {
        return startFrame;
    }

    public void setStartFrame(int startFrame) {
        this.startFrame = startFrame;
    }

    public int getEndFrame() {
        return endFrame;
    }

    public void setEndFrame(int endFrame) {
        this.endFrame = endFrame;
    }

    public double getFourierScale() {
        return fourierScale;
    }

    public void setFourierScale(double fourierScale) {
        this.fourierScale = fourierScale;
    }

    public MoCapScene getScene() {
        return scene;
    }

    public void setScene(MoCapScene scene) {
        this.scene = scene;
    }

    public Offset getOffset() {
        return offset;
    }

    public void setOffset(Offset offset) {
        this.offset = offset;
    }
}
