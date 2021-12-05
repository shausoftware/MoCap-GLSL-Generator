package com.shau.mocap.domain;

import java.io.Serializable;

public class AnalysedFrame implements Serializable {

    private String data;
    private int numberOfJoints;
    private int firstNonZeroJoint;
    private int firstZeroJointAfterData;

    public  AnalysedFrame() {
    }

    public AnalysedFrame(String data, int numberOfJoints, int firstNonZeroJoint, int firstZeroJointAfterData) {
        this.data = data;
        this.numberOfJoints = numberOfJoints;
        this.firstNonZeroJoint = firstNonZeroJoint;
        this.firstZeroJointAfterData = firstZeroJointAfterData;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getNumberOfJoints() {
        return numberOfJoints;
    }

    public void setNumberOfJoints(int numberOfJoints) {
        this.numberOfJoints = numberOfJoints;
    }

    public int getFirstNonZeroJoint() {
        return firstNonZeroJoint;
    }

    public void setFirstNonZeroJoint(int firstNonZeroJoint) {
        this.firstNonZeroJoint = firstNonZeroJoint;
    }

    public int getFirstZeroJointAfterData() {
        return firstZeroJointAfterData;
    }

    public void setFirstZeroJointAfterData(int firstZeroJointAfterData) {
        this.firstZeroJointAfterData = firstZeroJointAfterData;
    }
}
