package com.shau.mocap.parser;

import com.shau.mocap.domain.Joint;
import com.shau.mocap.exception.ParserException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrcParser {

    public List<String> validateHeaderAndReturnMocapData(List<String> lines) throws ParserException {
        if (lines.size() < 6) {
            throw new ParserException("Invalid TRC header size");
        }
        if (!lines.get(0).startsWith("PathFileType")) {
            throw new ParserException("Expecting PathFileType in TRC header");
        }
        String line = lines.get(5);
        if (lines.get(5).length() > 0) {
            throw new ParserException("Expecting empty line 5 in TRC header");
        }

        return lines.subList(6, lines.size());
    }

    public List<Double> parseFrameData(String frame) throws ParserException {
        try {
            return Arrays.asList(frame.split("\\s+")).stream()
                    .filter(s -> s != null)
                    .mapToDouble(s ->  Double.valueOf(s.trim()))
                    .boxed()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ParserException("Error parsing frame data: " + e.getMessage());
        }
    }

    public Integer parseFrameId(List<Double> values) throws ParserException {
        try {
            return values.get(0).intValue();
        } catch (Exception e) {
            throw new ParserException("Error parsing frame id: " + e.getMessage());
        }
    }

    public List<Joint> parseJoints(List<Double> values) {

        List<Joint> frameJoints = new ArrayList<>();
        int jointId = 1;

        for (int j = 0; j < values.size(); j++) {
            int dataIndex = (j - 2) % 3;

            if (j > 1) {

                if (dataIndex == 0 && (j + 2) < values.size()) {
                    Double value1 = values.get(j);
                    Double value2 = values.get(j + 1);
                    Double value3 = values.get(j + 2);
                    frameJoints.add(Joint.builder().id(jointId++).x(value1).y(value2).z(value3).build());
                }
            }
        }

        return frameJoints;
    }
}
