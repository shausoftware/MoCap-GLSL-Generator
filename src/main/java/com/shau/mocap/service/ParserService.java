package com.shau.mocap.service;

import com.shau.mocap.domain.*;
import com.shau.mocap.exception.ParserException;
import com.shau.mocap.parser.C3dParser;
import com.shau.mocap.parser.TrcParser;
import com.shau.mocap.parser.c3d.domain.C3dSpatialPoint;
import com.shau.mocap.parser.c3d.domain.C3dValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParserService {

    private static final String FILE_TRC = "trc";
    private static final String FILE_3CD = "c3d";

    private TrcParser trcParser;
    @Autowired
    public void setTrcParser(TrcParser trcParser) {
        this.trcParser = trcParser;
    }

    private C3dParser c3dParser;
    @Autowired
    public void setC3dParser(C3dParser c3dParser) {
        this.c3dParser  = c3dParser;
    }

    public MoCapScene parse(MultipartFile file) throws IOException, IllegalArgumentException, ParserException {

        MoCapScene scene = null;

        if (file.isEmpty())
            throw new IllegalArgumentException("Empty mocap data file");

        String filename = file.getOriginalFilename();
        String fileExtension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        if (FILE_TRC.equals(fileExtension)) {
            scene = parseTrcFile(filename, file.getInputStream());
        } else if (FILE_3CD.equals(fileExtension)) {
            scene = parseC3dFile(filename, file.getInputStream());
        } else {
            throw new IllegalArgumentException("Invalid mocap file extension");
        }

        return scene;
    }

    private MoCapScene parseTrcFile(String fileName, InputStream is) throws ParserException {

        List<String> lines = new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.toList());
        List<String> data = trcParser.validateHeaderAndReturnMocapData(lines);

        List<Frame> frames = new ArrayList<>();
        for (String frame : data) {
            try {
                List<Double> frameData = trcParser.parseFrameData(frame);
                int frameId = trcParser.parseFrameId(frameData);
                List<Joint> frameJoints = trcParser.parseJoints(frameData);
                frames.add(new Frame(frameId, frameJoints));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new MoCapScene(fileName, frames);
    }

    private MoCapScene parseC3dFile(String fileName, InputStream is) throws ParserException, IOException {

        List<C3dValue> dataValues = c3dParser.parseData(new BufferedInputStream(is));

        List<Frame> frames = new ArrayList<>();
        for (int i = 0; i < dataValues.size(); i++) {
            C3dValue dataValue = dataValues.get(i);
            List<Joint> joints = new ArrayList<>();
            for (int j = 0; j < dataValue.getSpatialPoints().size(); j++) {
                C3dSpatialPoint spatialPoint = dataValue.getSpatialPoints().get(j);
                joints.add(new Joint(j + 1,
                        Double.valueOf(spatialPoint.getX()),
                        Double.valueOf(spatialPoint.getY()),
                        Double.valueOf(spatialPoint.getZ())));
            }
            frames.add(new Frame(i + 1, joints));
        }

        return new MoCapScene(fileName, frames);
    }
}
