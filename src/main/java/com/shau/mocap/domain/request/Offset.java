package com.shau.mocap.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Offset {
    private Integer jointId;
    private Double x;
    private Double y;
    private Double z;
}
