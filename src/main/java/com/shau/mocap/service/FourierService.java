package com.shau.mocap.service;

import com.shau.mocap.domain.Frame;
import com.shau.mocap.domain.MoCapScene;
import com.shau.mocap.domain.request.Offset;
import com.shau.mocap.fourier.*;
import com.shau.mocap.fourier.domain.FixedJoint;
import com.shau.mocap.fourier.domain.FourierJoint;
import com.shau.mocap.fourier.domain.FourierTransform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Fourier Logic adapted from Inigo Quilez
 * https://www.shadertoy.com/view/4lGSDw
 * https://www.shadertoy.com/view/ltKSWD
 * Encoding logic adapted from Reinder Nijhoff
 * https://www.shadertoy.com/view/XtcyW4
 */

@Service
public class FourierService implements FourierConstants {

    private FourierTransformer fourierTransformer;
    private FourierPreProcessor fourierPreProcessor;
    @Autowired
    public void setFourierTransformer(FourierTransformer fourierTransformer) {
        this.fourierTransformer = fourierTransformer;
    }
    @Autowired
    public void setFourierPreProcessor(FourierPreProcessor fourierPreProcessor) {
        this.fourierPreProcessor = fourierPreProcessor;
    }

    public String generateFourier(MoCapScene moCapScene,
                                  int startFrame,
                                  int endFrame,
                                  boolean useEasing,
                                  int  easingFrames,
                                  double fourierScale,
                                  Offset offset,
                                  int fourierFrames,
                                  boolean useLowRes,
                                  int lowResStartFrame) {

        //pre-processing
        List<Frame> processFrames = fourierPreProcessor.scaleAndOffsetScene(moCapScene, offset, fourierScale);
        //if (useEasing) {
        //    processFrames = fourierTransformer.easing(processFrames, startFrame, endFrame, easingFrames);
        //}
        Map<Integer, FixedJoint> fixedJoints = fourierPreProcessor.findFixedJoints(processFrames);

        //fourier transform
        FourierTransform fourierTransform = fourierTransformer.createTransform(processFrames,
                startFrame,
                endFrame,
                fourierFrames);
        FourierBounds fourierBounds = fourierTransformer.fourierBounds(fourierTransform);
        LowResBounds lowResBounds = fourierTransformer.lowResBounds(fourierTransform, lowResStartFrame, fourierBounds);

        return generateFourierOutput(moCapScene.getOriginalFileName(),
                moCapScene.getFilename(),
                fourierTransform,
                fixedJoints,
                startFrame,
                endFrame,
                fourierFrames,
                fourierScale,
                fourierBounds,
                useLowRes,
                lowResBounds,
                lowResStartFrame);
    }

    private String generateFourierOutput(String originalFileName,
                                         String fileName,
                                         FourierTransform fourierTransform,
                                         Map<Integer, FixedJoint> fixedJoints,
                                         int startFrame,
                                         int endFrame,
                                         int fourierFrames,
                                         double fourierScale,
                                         FourierBounds fourierBounds,
                                         boolean useLowRes,
                                         LowResBounds lowResBounds,
                                         int lowResStartFrame) {

        String LS = System.lineSeparator();
        GLSLGenerator glslGenerator = new GLSLGenerator(originalFileName,
                fileName,
                startFrame,
                endFrame,
                lowResStartFrame,
                fourierFrames,
                fourierScale,
                fourierTransform.getFourierJoints().size(),
                fourierBounds,
                lowResBounds);

        int hiResJointLength = useLowRes ? lowResStartFrame :
                fourierTransform.getFourierJoints().get(0).getFourierFrames().size();
        int lowResJointLength = useLowRes ?
                fourierTransform.getFourierJoints().get(0).getFourierFrames().size() - lowResStartFrame : 0;

        StringBuilder common = new StringBuilder();
        glslGenerator.generateTitle(common,useLowRes);
        glslGenerator.generateCommon(common);
        StringBuilder shader = new StringBuilder();
        glslGenerator.generateCodeDefines(shader, useLowRes);
        glslGenerator.generateDecode16bitCode(shader);
        if (useLowRes)
            glslGenerator.generateDecode8bitCode(shader);
        glslGenerator.generatePosDCode(shader, useLowRes);
        if (useLowRes)
            glslGenerator.generatePosDLowResCode(shader);
        glslGenerator.generateCodeMainStart(shader, useLowRes, hiResJointLength,lowResJointLength);

        int  i = 0;
        for (FourierJoint fourierJoint : fourierTransform.getFourierJoints()) {
            if (i == 0) {
                shader.append("    if (U==J").append(i + 1).append(") {").append(LS);
            } else {
                shader.append("    else if (U==J").append(i + 1).append(") {").append(LS);
            }
            StringBuilder encBuffer = new StringBuilder();
            glslGenerator.generateCodeHiResData(encBuffer, fourierJoint, hiResJointLength, fixedJoints);
            if (useLowRes) {
                glslGenerator.generateCodeLowResData(encBuffer, fourierJoint, lowResJointLength, fixedJoints);
            }
            shader.append(encBuffer);
            shader.append(glslGenerator.generatePosData(fixedJoints.get(fourierJoint.getJointId())));
            shader.append("    }").append(LS);
            i++;
        }
        glslGenerator.generateCodeMainEnd(shader, useLowRes);
        StringBuilder debug = new StringBuilder();
        glslGenerator.generateDebug(debug);
        return common.append(shader).append(debug).toString();
    }
}
