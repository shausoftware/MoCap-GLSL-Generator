package com.shau.mocap.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Frame implements Serializable {

    private int id;
    private List<Joint> joints;

    public Frame() {

    }

    public Frame(int id, List<Joint> joints) {
        this.id = id;
        this.joints = joints;
    }

    public Bounds getBounds() {
        return new Bounds(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Joint> getJoints() {
        return joints;
    }

    public void setJoints(List<Joint> joints) {
        this.joints = joints;
    }
}
