package com.shau.mocap.fourier.utils;

import com.shau.mocap.domain.Frame;
import com.shau.mocap.domain.Joint;
import com.shau.mocap.domain.MoCapScene;

import java.util.List;
import java.util.stream.Collectors;

public class FourierJointFinder {

    public static List<Joint> findJointsByIndex(List<Frame> frames, int index) {
        return frames.stream()
                .map(f -> f.getJoints().get(index))
                .collect(Collectors.toList());
    }
}
