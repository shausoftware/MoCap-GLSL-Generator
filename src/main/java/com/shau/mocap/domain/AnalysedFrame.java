package com.shau.mocap.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnalysedFrame {
    private String data;
    private int numberOfJoints;
    private int firstNonZeroJoint;
    private int firstZeroJointAfterData;
}
