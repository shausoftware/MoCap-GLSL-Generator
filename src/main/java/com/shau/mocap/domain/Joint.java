package com.shau.mocap.domain;

import java.io.Serializable;

public class Joint implements Serializable {

    private int id;
    private Double x;
    private Double y;
    private Double z;
    private String colour = "white";
    private boolean display = true;

    public Joint() {
        super();
    }

    public Joint (int id, Double x, Double y, Double z) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Joint (int id, Double x, Double y, Double z, String colour, boolean display) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.colour = colour;
        this.display = display;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }


    /*
    public void updateDisplayState(String colour, boolean display) {
        this.colour = colour;
        this.display = display;
    }
    */

    /*
    public void updatePosition(Double x, Double y, Double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    */


}
