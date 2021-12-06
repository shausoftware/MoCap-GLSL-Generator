package com.shau.mocap.service;

import com.shau.mocap.domain.Frame;
import com.shau.mocap.domain.Joint;
import com.shau.mocap.domain.MoCapScene;
import com.shau.mocap.domain.request.Offset;
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

    private FourierTransformer fourierTransformer;
    @Autowired
    public void setFourierTransformer(FourierTransformer fourierTransformer) {
        this.fourierTransformer = fourierTransformer;
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

        int minX = Integer.MAX_VALUE;
        int maxX = -Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = -Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = -Integer.MAX_VALUE;

        for (int i = 0; i < transform.length; i++) {
            Double[][] dd = transform[i];
            for (int j = 0; j < dd.length; j++) {
                Double[] d = dd[j];
                minX = lower(minX, d[0]);
                minX = lower(minX, d[1]);
                maxX = higher(maxX, d[0]);
                maxX = higher(maxX, d[1]);
                minY = lower(minY, d[2]);
                minY = lower(minY, d[3]);
                maxY = higher(maxY, d[2]);
                maxY = higher(maxY, d[3]);
                minZ = lower(minZ, d[4]);
                minZ = lower(minZ, d[5]);
                maxZ = higher(maxZ, d[4]);
                maxZ = higher(maxZ, d[5]);
            }
        }

        //push negative fourier values to zero
        Integer xOffs = minX < 0 ? Math.abs(minX) : 0;
        Integer yOffs = minY < 0 ? Math.abs(minY) : 0;
        Integer zOffs = minZ < 0 ? Math.abs(minZ) : 0;

        int lowResMinX = Integer.MAX_VALUE;
        int lowResMaxX = -Integer.MAX_VALUE;
        int lowResMinY = Integer.MAX_VALUE;
        int lowResMaxY = -Integer.MAX_VALUE;
        int lowResMinZ = Integer.MAX_VALUE;
        int lowResMaxZ = -Integer.MAX_VALUE;

        for (int i = 0; i < transform.length; i++) {
            Double[][] dd = transform[i];
            for (int j = cutoff; j < dd.length; j++) {
                Double[] d = dd[j];
                lowResMinX = lower(lowResMinX, d[0] + xOffs);
                lowResMinX = lower(lowResMinX, d[1] + xOffs);
                lowResMaxX = higher(lowResMaxX, d[0] + xOffs);
                lowResMaxX = higher(lowResMaxX, d[1] + xOffs);
                lowResMinY = lower(lowResMinY, d[2] + yOffs);
                lowResMinY = lower(lowResMinY, d[3] + yOffs);
                lowResMaxY = higher(lowResMaxY, d[2] + yOffs);
                lowResMaxY = higher(lowResMaxY, d[3] + yOffs);
                lowResMinZ = lower(lowResMinZ, d[4] + zOffs);
                lowResMinZ = lower(lowResMinZ, d[5] + zOffs);
                lowResMaxZ = higher(lowResMaxZ, d[4] + zOffs);
                lowResMaxZ = higher(lowResMaxZ, d[5] + zOffs);
            }
        }

        //maximum deviation in fourier data required for low resolution data
        int maxLowResDevX = lowResMaxX - lowResMinX;
        int maxLowResDevY = lowResMaxY - lowResMinY;
        int maxLowResDevZ = lowResMaxZ - lowResMinZ;

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
            commonBuffer.append("   Low Resolution Cutoff: ").append(cutoff).append(LS);
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
            shaderCode.append("#define lowResXOffs ").append(lowResMinX).append(".0").append(LS);
            shaderCode.append("#define lowResYOffs ").append(lowResMinY).append(".0").append(LS);
            shaderCode.append("#define lowResZOffs ").append(lowResMinZ).append(".0").append(LS);
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

                        int f1x1 = (int) ((frame1[0].intValue() + xOffs - lowResMinX) * lowResScaleEncodeX);
                        int f1x2 = (int) ((frame1[1].intValue() + xOffs - lowResMinX) * lowResScaleEncodeX);
                        int f1y1 = (int) ((frame1[2].intValue() + yOffs - lowResMinY) * lowResScaleEncodeY);
                        int f1y2 = (int) ((frame1[3].intValue() + yOffs - lowResMinY) * lowResScaleEncodeY);
                        int f1z1 = (int) ((frame1[4].intValue() + zOffs - lowResMinZ) * lowResScaleEncodeZ);
                        int f1z2 = (int) ((frame1[5].intValue() + zOffs - lowResMinZ) * lowResScaleEncodeZ);
                        int f2x1 = (int) ((frame2[0].intValue() + xOffs - lowResMinX) * lowResScaleEncodeX);
                        int f2x2 = (int) ((frame2[1].intValue() + xOffs - lowResMinX) * lowResScaleEncodeX);
                        int f2y1 = (int) ((frame2[2].intValue() + yOffs - lowResMinY) * lowResScaleEncodeY);
                        int f2y2 = (int) ((frame2[3].intValue() + yOffs - lowResMinY) * lowResScaleEncodeY);
                        int f2z1 = (int) ((frame2[4].intValue() + zOffs - lowResMinZ) * lowResScaleEncodeZ);
                        int f2z2 = (int) ((frame2[5].intValue() + zOffs - lowResMinZ) * lowResScaleEncodeZ);

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
        debug.append("  minX: ").append(minX).append(LS);
        debug.append("  maxX: ").append(maxX).append(LS);
        if (maxX - minX > MAX_16_BIT) {
            debug.append("  WARNING: Try reducing fourier scale value. X differential is greater than ")
                    .append(MAX_16_BIT)
                    .append(LS);
        }
        debug.append("  minY: ").append(minY).append(LS);
        debug.append("  maxY: ").append(maxY).append(LS);
        if (maxY - minY > MAX_16_BIT) {
            debug.append("  WARNING: Try reducing fourier scale value. Y differential is greater than ")
                    .append(MAX_16_BIT)
                    .append(LS);
        }
        debug.append("  minZ: ").append(minZ).append(LS);
        debug.append("  maxZ: ").append(maxZ).append(LS);
        if (maxZ - minZ > MAX_16_BIT) {
            debug.append("  WARNING: Try reducing fourier scale value. Z differential is greater than ")
                    .append(MAX_16_BIT)
                    .append(LS);
        }
        debug.append("  max low res x: ").append(maxLowResDevX).append(LS);
        debug.append("  max low res y: ").append(maxLowResDevY).append(LS);
        debug.append("  max low res z: ").append(maxLowResDevZ).append(LS);
        debug.append("  lowResMinX: ").append(lowResMinX).append(LS);
        debug.append("  lowResMaxX: ").append(lowResMaxX).append(LS);
        if (lowResMaxX - lowResMinX > MAX_8_BIT) {
            debug.append("  WARNING LOW RES X differential is greater than ")
                    .append(MAX_8_BIT)
                    .append(LS);
        }
        debug.append("  lowResMinY: ").append(lowResMinY).append(LS);
        debug.append("  lowResMaxY: ").append(lowResMaxY).append(LS);
        if (lowResMaxY - lowResMinY > MAX_8_BIT) {
            debug.append("  WARNING LOW RES Y differential is greater than ")
                    .append(MAX_8_BIT)
                    .append(LS);
        }
        debug.append("  lowResMinZ: ").append(lowResMinZ).append(LS);
        debug.append("  lowResMaxZ: ").append(lowResMaxZ).append(LS);
        if (lowResMaxZ - lowResMinZ > MAX_8_BIT) {
            debug.append("  WARNING LOW RES Z differential is greater than ")
                    .append(MAX_8_BIT)
                    .append(LS);
        }
        debug.append("*/").append(LS);

        return commonBuffer.append(shaderCode).append(debug);
    }

    private int higher(int cMax, Double val) {
        if (val > cMax)
            return val.intValue();
        return cMax;
    }

    private int lower(int cMin, Double val) {
        if (val < cMin)
            return val.intValue();
        return cMin;
    }
}
