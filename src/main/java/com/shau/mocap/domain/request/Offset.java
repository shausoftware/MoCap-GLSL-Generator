package com.shau.mocap.domain.request;

import javax.swing.plaf.PanelUI;
import java.io.Serializable;

public class Offset implements Serializable {

    private Integer jointId;
    private Double x;
    private Double y;
    private Double z;

    public Offset() {
        super();
    }

    public Offset(Integer jointId, Double x, Double y, Double z) {
        this.jointId  = jointId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Integer getJointId() {
        return jointId;
    }

    public void setJointId(Integer jointId) {
        this.jointId = jointId;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
    }
}
