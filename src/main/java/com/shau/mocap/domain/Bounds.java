package com.shau.mocap.domain;

import lombok.Data;

@Data
public class Bounds {
    private Double minX = Double.MAX_VALUE;
    private Double maxX = -Double.MAX_VALUE;
    private Double minY = Double.MAX_VALUE;
    private Double maxY = -Double.MAX_VALUE;
    private Double minZ = Double.MAX_VALUE;
    private Double maxZ = -Double.MAX_VALUE;

    public Bounds(MoCapScene moCapScene) {
        moCapScene.getFrames().stream().flatMap(f -> f.getJoints().stream()).forEach(j -> {
            compareJoint(j);
        });
    }

    public Bounds(Frame frame) {
        frame.getJoints().forEach(j ->  {
            compareJoint(j);
        });
    }

    private void compareJoint(Joint joint) {
        if (joint.isDisplay()) {
            if (joint.getX() < minX) minX = joint.getX();
            if (joint.getX() > maxX) maxX = joint.getX();
            if (joint.getY() < minY) minY = joint.getY();
            if (joint.getY() > maxY) maxY = joint.getY();
            if (joint.getZ() < minZ) minZ = joint.getZ();
            if (joint.getZ() > maxZ) maxZ = joint.getZ();
        }
    }
}
