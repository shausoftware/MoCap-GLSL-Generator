package com.shau.mocap.service;

import com.shau.mocap.domain.Frame;
import com.shau.mocap.domain.Joint;
import com.shau.mocap.domain.MoCapScene;
import com.shau.mocap.domain.request.Offset;
import com.shau.mocap.fourier.FourierPreProcessor;
import com.shau.mocap.fourier.FourierTransformer;
import com.shau.mocap.parser.BinaryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FourierService {

    private static final int MAX_16_BIT = 65353;
    private static final int MAX_8_BIT = 255;

    private static final int MINX = 0;
    private static final int MAXX = 1;
    private static final int MINY = 2;
    private static final int MAXY = 3;
    private static final int MINZ = 4;
    private static final int MAXZ = 5;

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
        List<Frame> processFrames = fourierTransformer.preProcess(moCapScene, offset, fourierScale);
        if (useEasing) {
            processFrames = fourierTransformer.easing(processFrames, startFrame, endFrame, easingFrames);
        }

        int dataFramesSize = endFrame - startFrame;
        int allJointsSize = processFrames.get(startFrame).getJoints().size();
        //displayed joints
        int displayedJointsSize = (int) processFrames.get(startFrame).getJoints().stream()
                .filter(Joint::isDisplay)
                .count();

        //fourier transform
        Double[][][] transform = new Double[displayedJointsSize][fourierFrames][6];
        int jointCount = 0;
        for (int i = 0;  i < allJointsSize; i++) {
            int index = i;
            List<Joint> fourierJoints = processFrames.stream()
                    .skip(startFrame)
                    .limit(dataFramesSize)
                    .map(f -> f.getJoints().get(index))
                    .collect(Collectors.toList());

            if (fourierJoints.get(0).isDisplay()) {
                transform[jointCount++] = fourierTransformer.calculateFourier(fourierJoints, fourierFrames);
            }
        }

        StringBuffer shaderBuffer = generateFourierOutput(moCapScene.getOriginalFileName(),
                moCapScene.getFilename(),
                startFrame,
                endFrame,
                fourierScale,
                transform,
                dataFramesSize,
                fourierFrames,
                useLowRes,
                lowResStartFrame);

        return shaderBuffer.toString();
    }

    //TODO: refactor
    private StringBuffer generateFourierOutput(String originalFileName,
                                               String fileName,
                                               int startFrame,
                                               int endFrame,
                                               double scale,
                                               Double[][][] transform,
                                               int frames,
                                               int fourierFrames,
                                               boolean useLowResolution,
                                               int cutoff) {

        /* OUTPUT */
        /* Change to suit your needs*/

        String LS = System.lineSeparator();

        int[] bounds = fourierPreProcessor.fourierBounds(transform, 0, 0, 0, 0);
        //push negative fourier values to zero
        Integer xOffs = bounds[MINX] < 0 ? Math.abs(bounds[MINX]) : 0;
        Integer yOffs = bounds[MINY] < 0 ? Math.abs(bounds[MINY]) : 0;
        Integer zOffs = bounds[MINZ] < 0 ? Math.abs(bounds[MINZ]) : 0;

        int[] lowResBounds = fourierPreProcessor.fourierBounds(transform, cutoff, xOffs, yOffs, zOffs);
        //maximum deviation in fourier data required for low resolution data
        int maxLowResDevX = lowResBounds[MAXX] - lowResBounds[MINX];
        int maxLowResDevY = lowResBounds[MAXY] - lowResBounds[MINY];
        int maxLowResDevZ = lowResBounds[MAXZ] - lowResBounds[MINZ];

        //low res scaling
        float lowResScaleEncodeX = maxLowResDevX < 255 ? 1.0f : 255.0f / (float) maxLowResDevX;
        float lowResScaleDecodeX = maxLowResDevX < 255 ? 1.0f : (float) maxLowResDevX / 255.0f;
        float lowResScaleEncodeY = maxLowResDevY < 255 ? 1.0f : 255.0f / (float) maxLowResDevY;
        float lowResScaleDecodeY = maxLowResDevY < 255 ? 1.0f : (float) maxLowResDevY / 255.0f;
        float lowResScaleEncodeZ = maxLowResDevZ < 255 ? 1.0f : 255.0f / (float) maxLowResDevZ;
        float lowResScaleDecodeZ = maxLowResDevZ < 255 ? 1.0f : (float) maxLowResDevZ / 255.0f;

        StringBuffer commonBuffer = new StringBuffer();
        commonBuffer.append("/*").append(LS);
        commonBuffer.append("   Original File Name: ").append(originalFileName).append(LS);
        commonBuffer.append("   Project File Name: ").append(fileName).append(LS);
        commonBuffer.append("   Start Frame: ").append(startFrame).append(LS);
        commonBuffer.append("   End  Frame: ").append(endFrame).append(LS);
        commonBuffer.append("   Fourier Frames: ").append(fourierFrames).append(LS);
        commonBuffer.append("   Use Low Resolution Encoding: ").append(useLowResolution).append(LS);
        if (useLowResolution) {
            commonBuffer.append("   Low Resolution Start Frame ").append(cutoff).append(LS);
        }
        commonBuffer.append("   Fourier Scale: ").append(scale).append(LS);
        commonBuffer.append("*/").append(LS);
        commonBuffer.append("/* MOVE TO COMMON - START */").append(LS);
        StringBuilder shaderCode = new StringBuilder();
        shaderCode.append("//fourier frames").append(LS);
        if (useLowResolution) {
            shaderCode.append("#define FFRAMES ").append(cutoff).append(LS);
            shaderCode.append("#define FFRAMES_LOW_RES ").append((fourierFrames - cutoff) / 2).append(LS);
        } else {
            shaderCode.append("#define FFRAMES ").append(fourierFrames).append(LS);
        }
        shaderCode.append("//original number of frames sampled").append(LS);
        shaderCode.append("#define OFS ").append(frames).append(".0").append(LS);
        shaderCode.append("#define xOffs ").append(xOffs).append(".0").append(LS);
        shaderCode.append("#define yOffs ").append(yOffs).append(".0").append(LS);
        shaderCode.append("#define zOffs ").append(zOffs).append(".0").append(LS);
        if (useLowResolution) {
            shaderCode.append("#define lowResXOffs ").append(lowResBounds[MINX]).append(".0").append(LS);
            shaderCode.append("#define lowResYOffs ").append(lowResBounds[MINY]).append(".0").append(LS);
            shaderCode.append("#define lowResZOffs ").append(lowResBounds[MINZ]).append(".0").append(LS);
            shaderCode.append("#define lowResScaleDecodeX ").append(lowResScaleDecodeX).append(LS);
            shaderCode.append("#define lowResScaleDecodeY ").append(lowResScaleDecodeY).append(LS);
            shaderCode.append("#define lowResScaleDecodeZ ").append(lowResScaleDecodeZ).append(LS);
        }
        shaderCode.append(LS);
        shaderCode.append("vec2 decode16bit(uint d) {").append(LS);
        shaderCode.append("    return vec2(d >> 16U, d & 0x0000FFFFU);").append(LS);
        shaderCode.append("}").append(LS);
        shaderCode.append(LS);
        if (useLowResolution) {
            shaderCode.append("vec2 decode8bit(uint d, uint offset) {").append(LS);
            shaderCode.append("    return vec2((d >> offset) & 0xFFU, (d >> offset + 8U) & 0xFFU);").append(LS);
            shaderCode.append("}").append(LS);
            shaderCode.append(LS);
        }
        shaderCode.append("vec3 posD(uint[FFRAMES] eX, uint[FFRAMES] eY, uint[FFRAMES] eZ, float h, vec2 U) {").append(LS);
        shaderCode.append("    vec3 q = vec3(0.0);").append(LS);
        shaderCode.append("    for (int k=0; k<FFRAMES; k++) {").append(LS);
        if (useLowResolution) {
            shaderCode.append("        float w = (k==0) ? 1.0 : 2.0;").append(LS);
        } else {
            shaderCode.append("        float w = (k==0||k==(FFRAMES - 1)) ? 1.0 : 2.0;").append(LS);
        }
        shaderCode.append("        float an = -6.283185*float(k)*h;").append(LS);
        shaderCode.append("        vec2 ex = vec2(cos(an), sin(an));").append(LS);
        shaderCode.append("        q.x += w*dot(decode16bit(eX[k]) - xOffs,ex)/OFS;").append(LS);
        shaderCode.append("        q.y += w*dot(decode16bit(eY[k]) - yOffs,ex)/OFS;").append(LS);
        shaderCode.append("        q.z += w*dot(decode16bit(eZ[k]) - zOffs,ex)/OFS;").append(LS);
        shaderCode.append("    }").append(LS);
        shaderCode.append("    return q;").append(LS);
        shaderCode.append("}").append(LS);
        shaderCode.append(LS);
        if (useLowResolution) {
            shaderCode.append("vec3 posD_LowRes(uint[FFRAMES_LOW_RES] eXLowRes, uint[FFRAMES_LOW_RES] eYLowRes, uint[FFRAMES_LOW_RES] eZLowRes, float h, vec2 U) {").append(LS);
            shaderCode.append("    vec3 q = vec3(0.0);").append(LS);
            shaderCode.append("    int fourierIndex = FFRAMES;").append(LS);
            shaderCode.append("    for (int k=0; k<FFRAMES_LOW_RES; k++) {").append(LS);
            shaderCode.append("        float w = 2.0;").append(LS);
            shaderCode.append("        float an = -6.283185*float(fourierIndex)*h;").append(LS);
            shaderCode.append("        vec2 ex = vec2(cos(an), sin(an));").append(LS);
            shaderCode.append("        q.x += w*dot(decode8bit(eXLowRes[k],0U)*lowResScaleDecodeX + lowResXOffs - xOffs,ex)/OFS;").append(LS);
            shaderCode.append("        q.y += w*dot(decode8bit(eYLowRes[k],0U)*lowResScaleDecodeY + lowResYOffs - yOffs,ex)/OFS;").append(LS);
            shaderCode.append("        q.z += w*dot(decode8bit(eZLowRes[k],0U)*lowResScaleDecodeZ + lowResZOffs - zOffs,ex)/OFS;").append(LS);
            shaderCode.append("        fourierIndex++;").append(LS);
            shaderCode.append(LS);
            shaderCode.append("        w = fourierIndex < (FFRAMES + FFRAMES_LOW_RES*2) - 1 ? 1.0 : 2.0;").append(LS);
            shaderCode.append("        an = -6.283185*float(fourierIndex)*h;").append(LS);
            shaderCode.append("        ex = vec2(cos(an), sin(an));").append(LS);
            shaderCode.append("        q.x += w*dot(decode8bit(eXLowRes[k],16U)*lowResScaleDecodeX + lowResXOffs - xOffs,ex)/OFS;").append(LS);
            shaderCode.append("        q.y += w*dot(decode8bit(eYLowRes[k],16U)*lowResScaleDecodeY + lowResYOffs - yOffs,ex)/OFS;").append(LS);
            shaderCode.append("        q.z += w*dot(decode8bit(eZLowRes[k],16U)*lowResScaleDecodeZ + lowResZOffs - zOffs,ex)/OFS;").append(LS);
            shaderCode.append("        fourierIndex++;").append(LS);
            shaderCode.append("    }").append(LS);
            shaderCode.append("    return q;").append(LS);
            shaderCode.append("}").append(LS);
            shaderCode.append(LS);
        }
        shaderCode.append("void mainImage(out vec4 C, vec2 U) {").append(LS);
        shaderCode.append(LS);
        shaderCode.append("    float h = mod(floor(T*8.), ")
                .append(fourierFrames)
                .append(".) / ")
                .append(fourierFrames)
                .append(".;")
                .append(LS);
        shaderCode.append("    uint eX[FFRAMES], eY[FFRAMES], eZ[FFRAMES];").append(LS);
        if (useLowResolution) {
            shaderCode.append("    uint eXLowRes[FFRAMES_LOW_RES], eYLowRes[FFRAMES_LOW_RES], eZLowRes[FFRAMES_LOW_RES];").append(LS);
        }
        shaderCode.append(LS);

        for (int i = 0; i < transform.length;  i++) {

            commonBuffer.append("#define J").append(i + 1).append(" vec2(").append(i).append(".5, 0.5)").append(LS);

            if (i == 0) {
                shaderCode.append("    if (U==J").append(i + 1).append(") {").append(LS);
            } else {
                shaderCode.append("    else if (U==J").append(i + 1).append(") {").append(LS);
            }

            Double[][] joint = transform[i];
            int hiResJointLength = useLowResolution ? cutoff : joint.length;
            int lowResJointLength = useLowResolution ? joint.length - cutoff : 0;

            StringBuilder encBufferX = new StringBuilder();
            encBufferX.append("        eX = uint[").append(hiResJointLength).append("] (");
            StringBuilder encBufferY = new StringBuilder();
            encBufferY.append("        eY = uint[").append(hiResJointLength).append("] (");
            StringBuilder encBufferZ = new StringBuilder();
            encBufferZ.append("        eZ = uint[").append(hiResJointLength).append("] (");

            //hi resolution joint data
            for (int j = 0; j < hiResJointLength; j++) {

                Double[] frame = joint[j];

                //offset to zero base data
                int x1 = frame[0].intValue() + xOffs;
                int x2 = frame[1].intValue() + xOffs;
                int y1 = frame[2].intValue() + yOffs;
                int y2 = frame[3].intValue() + yOffs;
                int z1 = frame[4].intValue() + zOffs;
                int z2 = frame[5].intValue() + zOffs;

                encBufferX.append("0x").append(Integer.toHexString(BinaryHelper.encode(x1, x2))).append("U");
                encBufferY.append("0x").append(Integer.toHexString(BinaryHelper.encode(y1, y2))).append("U");
                encBufferZ.append("0x").append(Integer.toHexString(BinaryHelper.encode(z1, z2))).append("U");

                if (j < hiResJointLength - 1) {
                    encBufferX.append(",");
                    encBufferY.append(",");
                    encBufferZ.append(",");
                }
            }

            encBufferX.append(");").append(LS);
            encBufferY.append(");").append(LS);
            encBufferZ.append(");").append(LS);

            //low resolution joint data
            if (useLowResolution) {
                encBufferX.append("        eXLowRes = uint[").append(lowResJointLength / 2).append("] (");
                encBufferY.append("        eYLowRes = uint[").append(lowResJointLength / 2).append("] (");
                encBufferZ.append("        eZLowRes = uint[").append(lowResJointLength / 2).append("] (");

                for (int k = 0; k < lowResJointLength; k++) {
                    if (k % 2 == 1) {

                        Double[] frame1 = joint[k - 1 + cutoff];
                        Double[] frame2 = joint[k + cutoff];

                        int f1x1 = (int) ((frame1[0].intValue() + xOffs - lowResBounds[MINX]) * lowResScaleEncodeX);
                        int f1x2 = (int) ((frame1[1].intValue() + xOffs - lowResBounds[MINX]) * lowResScaleEncodeX);
                        int f1y1 = (int) ((frame1[2].intValue() + yOffs - lowResBounds[MINY]) * lowResScaleEncodeY);
                        int f1y2 = (int) ((frame1[3].intValue() + yOffs - lowResBounds[MINY]) * lowResScaleEncodeY);
                        int f1z1 = (int) ((frame1[4].intValue() + zOffs - lowResBounds[MINZ]) * lowResScaleEncodeZ);
                        int f1z2 = (int) ((frame1[5].intValue() + zOffs - lowResBounds[MINZ]) * lowResScaleEncodeZ);
                        int f2x1 = (int) ((frame2[0].intValue() + xOffs - lowResBounds[MINX]) * lowResScaleEncodeX);
                        int f2x2 = (int) ((frame2[1].intValue() + xOffs - lowResBounds[MINX]) * lowResScaleEncodeX);
                        int f2y1 = (int) ((frame2[2].intValue() + yOffs - lowResBounds[MINY]) * lowResScaleEncodeY);
                        int f2y2 = (int) ((frame2[3].intValue() + yOffs - lowResBounds[MINY]) * lowResScaleEncodeY);
                        int f2z1 = (int) ((frame2[4].intValue() + zOffs - lowResBounds[MINZ]) * lowResScaleEncodeZ);
                        int f2z2 = (int) ((frame2[5].intValue() + zOffs - lowResBounds[MINZ]) * lowResScaleEncodeZ);

                        encBufferX.append("0x").append(Integer.toHexString(BinaryHelper.encode(f1x1, f1x2, f2x1, f2x2))).append("U");
                        encBufferY.append("0x").append(Integer.toHexString(BinaryHelper.encode(f1y1, f1y2, f2y1, f2y2))).append("U");
                        encBufferZ.append("0x").append(Integer.toHexString(BinaryHelper.encode(f1z1, f1z2, f2z1, f2z2))).append("U");

                        if (k < lowResJointLength - 1) {
                            encBufferX.append(",");
                            encBufferY.append(",");
                            encBufferZ.append(",");
                        }
                    }
                }

                encBufferX.append(");").append(LS);
                encBufferY.append(");").append(LS);
                encBufferZ.append(");").append(LS);
            }

            shaderCode.append(encBufferX);
            shaderCode.append(encBufferY);
            shaderCode.append(encBufferZ);
            shaderCode.append("    }").append(LS);
        }

        shaderCode.append("    vec3 q = posD(eX,eY,eZ,h,U);").append(LS);
        if (useLowResolution) {
            shaderCode.append("    q += posD_LowRes(eXLowRes,eYLowRes,eZLowRes,h,U);").append(LS);
        }
        shaderCode.append("    if (iFrame>0) {").append(LS);
        shaderCode.append("        q = mix(q,texelFetch(iChannel0, ivec2(U),0).xyz,0.7);").append(LS);
        shaderCode.append("    }").append(LS);

        shaderCode.append("    C = vec4(q.x,q.y,q.z,1.0);").append(LS);

        shaderCode.append("}").append(LS);

        commonBuffer.append("/* MOVE TO COMMON - END */").append(LS);

        //Debug Information
        StringBuilder debug = new StringBuilder();
        debug.append(LS);
        debug.append("/* Fourier Generation DEBUG Information").append(LS);
        debug.append("  minX: ").append(bounds[MINX]).append(LS);
        debug.append("  maxX: ").append(bounds[MAXX]).append(LS);
        if (bounds[MAXX] - bounds[MINX] > MAX_16_BIT) {
            debug.append("  WARNING: Try reducing fourier scale value. X differential is greater than ")
                    .append(MAX_16_BIT)
                    .append(LS);
        }
        debug.append("  minY: ").append(bounds[MINY]).append(LS);
        debug.append("  maxY: ").append(bounds[MAXY]).append(LS);
        if (bounds[MAXY] - bounds[MINY] > MAX_16_BIT) {
            debug.append("  WARNING: Try reducing fourier scale value. Y differential is greater than ")
                    .append(MAX_16_BIT)
                    .append(LS);
        }
        debug.append("  minZ: ").append(bounds[MINZ]).append(LS);
        debug.append("  maxZ: ").append(bounds[MAXZ]).append(LS);
        if (bounds[MAXZ] - bounds[MINZ] > MAX_16_BIT) {
            debug.append("  WARNING: Try reducing fourier scale value. Z differential is greater than ")
                    .append(MAX_16_BIT)
                    .append(LS);
        }
        debug.append("  max low res x: ").append(maxLowResDevX).append(LS);
        debug.append("  max low res y: ").append(maxLowResDevY).append(LS);
        debug.append("  max low res z: ").append(maxLowResDevZ).append(LS);
        debug.append("  lowResMinX: ").append(lowResBounds[MINX]).append(LS);
        debug.append("  lowResMaxX: ").append(lowResBounds[MAXX]).append(LS);
        debug.append("  lowResMinY: ").append(lowResBounds[MINY]).append(LS);
        debug.append("  lowResMaxY: ").append(lowResBounds[MAXY]).append(LS);
        debug.append("  lowResMinZ: ").append(lowResBounds[MINZ]).append(LS);
        debug.append("  lowResMaxZ: ").append(lowResBounds[MAXZ]).append(LS);
        debug.append("*/").append(LS);

        return commonBuffer.append(shaderCode).append(debug);
    }
}
