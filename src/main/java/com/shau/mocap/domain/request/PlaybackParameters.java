package com.shau.mocap.domain.request;

import java.io.Serializable;

public class PlaybackParameters implements Serializable {

    private Integer startFrame;
    private Integer endFrame;
    private Integer frameDuration;
    private Double scale;
    private String view;

    public PlaybackParameters() {

    }

    public Integer getStartFrame() {
        return startFrame;
    }

    public void setStartFrame(Integer startFrame) {
        this.startFrame = startFrame;
    }

    public Integer getEndFrame() {
        return endFrame;
    }

    public void setEndFrame(Integer endFrame) {
        this.endFrame = endFrame;
    }

    public Integer getFrameDuration() {
        return frameDuration;
    }

    public void setFrameDuration(Integer frameDuration) {
        this.frameDuration = frameDuration;
    }

    public Double getScale() {
        return scale;
    }

    public void setScale(Double scale) {
        this.scale = scale;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }
}
