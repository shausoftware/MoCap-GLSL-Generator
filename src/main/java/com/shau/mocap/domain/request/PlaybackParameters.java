package com.shau.mocap.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaybackParameters {
    private Integer startFrame;
    private Integer endFrame;
    private Integer frameDuration;
    private Double scale;
    private String view;
}
