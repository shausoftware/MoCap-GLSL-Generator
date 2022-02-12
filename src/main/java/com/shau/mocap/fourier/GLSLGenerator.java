package com.shau.mocap.fourier;

import com.shau.mocap.fourier.domain.FixedJoint;
import com.shau.mocap.fourier.domain.FourierFrame;
import com.shau.mocap.fourier.domain.FourierJoint;
import com.shau.mocap.parser.BinaryHelper;

import java.util.Map;

public class GLSLGenerator implements FourierConstants {

    private static final String LS = System.lineSeparator();

    private final String originalFileName;
    private final String fileName;
    private final int startFrame;
    private final int endFrame;
    private final int cutoff;
    private final int fourierFrames;
    private final double scale;
    private final int transformLength;
    private final FourierBounds fourierBounds;
    private final LowResBounds lowResBounds;

    public GLSLGenerator(String originalFileName,
                         String fileName,
                         int startFrame,
                         int endFrame,
                         int cutoff,
                         int fourierFrames,
                         double scale,
                         int transformLength,
                         FourierBounds fourierBounds,
                         LowResBounds lowResBounds) {

        this.originalFileName = originalFileName;
        this.fileName = fileName;
        this.startFrame = startFrame;
        this.endFrame = endFrame;
        this.cutoff = cutoff;
        this.fourierFrames = fourierFrames;
        this.scale = scale;
        this.transformLength = transformLength;
        this.fourierBounds = fourierBounds;
        this.lowResBounds = lowResBounds;
    }

    public void generateTitle(StringBuilder sb, boolean useLowResolution) {
        sb.append("/*").append(LS);
        sb.append("   Original File Name: ").append(originalFileName).append(LS);
        sb.append("   Project File Name: ").append(fileName).append(LS);
        sb.append("   Start Frame: ").append(startFrame).append(LS);
        sb.append("   End  Frame: ").append(endFrame).append(LS);
        sb.append("   Fourier Frames: ").append(fourierFrames).append(LS);
        sb.append("   Fourier Scale: ").append(scale).append(LS);
        sb.append("   Use Low Resolution Encoding: ").append(useLowResolution).append(LS);
        if (useLowResolution) {
            sb.append("   Low Resolution Start Frame ").append(cutoff).append(LS);
        }
        sb.append("*/").append(LS);
    }

    public void generateCommon(StringBuilder sb) {
        sb.append("/* MOVE TO COMMON - START */").append(LS);
        for (int i = 0; i < transformLength;  i++) {
            sb.append("#define J").append(i + 1).append(" vec2(").append(i).append(".5, 0.5)").append(LS);
        }
        sb.append("/* MOVE TO COMMON - END */").append(LS);
    }

    public void generateCodeDefines(StringBuilder sb, boolean useLowResolution) {

        sb.append("//fourier frames").append(LS);
        if (useLowResolution) {
            sb.append("#define FFRAMES ").append(cutoff).append(LS);
            sb.append("#define FFRAMES_LOW_RES ").append((fourierFrames - cutoff) / 2).append(LS);
        } else {
            sb.append("#define FFRAMES ").append(fourierFrames).append(LS);
        }
        sb.append("//original number of frames sampled").append(LS);
        sb.append("#define OFS ").append(endFrame - startFrame).append(".0").append(LS);
        sb.append("#define xOffs ").append(fourierBounds.getXOffs()).append(".0").append(LS);
        sb.append("#define yOffs ").append(fourierBounds.getYOffs()).append(".0").append(LS);
        sb.append("#define zOffs ").append(fourierBounds.getZOffs()).append(".0").append(LS);
        if (useLowResolution) {
            sb.append("#define lowResXOffs ").append(lowResBounds.getBounds()[MINX]).append(".0").append(LS);
            sb.append("#define lowResYOffs ").append(lowResBounds.getBounds()[MINY]).append(".0").append(LS);
            sb.append("#define lowResZOffs ").append(lowResBounds.getBounds()[MINZ]).append(".0").append(LS);
            sb.append("#define lowResScaleDecodeX ").append(lowResBounds.getLowResScaleDecodeX()).append(LS);
            sb.append("#define lowResScaleDecodeY ").append(lowResBounds.getLowResScaleDecodeY()).append(LS);
            sb.append("#define lowResScaleDecodeZ ").append(lowResBounds.getLowResScaleDecodeZ()).append(LS);
        }
        sb.append(LS);
    }

    public void generateDecode16bitCode(StringBuilder sb) {
        sb.append("vec2 decode16bit(uint d) {").append(LS);
        sb.append("    return vec2(d >> 16U, d & 0x0000FFFFU);").append(LS);
        sb.append("}").append(LS);
        sb.append(LS);
    }

    public void generateDecode8bitCode(StringBuilder sb) {
        sb.append("vec2 decode8bit(uint d, uint offset) {").append(LS);
        sb.append("    return vec2((d >> offset) & 0xFFU, (d >> offset + 8U) & 0xFFU);").append(LS);
        sb.append("}").append(LS);
        sb.append(LS);
    }

    public void generatePosDCode(StringBuilder sb, boolean useLowResolution) {
        sb.append("vec3 posD(uint[FFRAMES] eX, uint[FFRAMES] eY, uint[FFRAMES] eZ, float h, vec2 U) {").append(LS);
        sb.append("    vec3 q = vec3(0.0);").append(LS);
        sb.append("    for (int k=0; k<FFRAMES; k++) {").append(LS);
        if (useLowResolution) {
            sb.append("        float w = (k==0) ? 1.0 : 2.0;").append(LS);
        } else {
            sb.append("        float w = (k==0||k==(FFRAMES - 1)) ? 1.0 : 2.0;").append(LS);
        }
        sb.append("        float an = -6.283185*float(k)*h;").append(LS);
        sb.append("        vec2 ex = vec2(cos(an), sin(an));").append(LS);
        sb.append("        q.x += w*dot(decode16bit(eX[k]) - xOffs,ex)/OFS;").append(LS);
        sb.append("        q.y += w*dot(decode16bit(eY[k]) - yOffs,ex)/OFS;").append(LS);
        sb.append("        q.z += w*dot(decode16bit(eZ[k]) - zOffs,ex)/OFS;").append(LS);
        sb.append("    }").append(LS);
        sb.append("    return q;").append(LS);
        sb.append("}").append(LS);
        sb.append(LS);
    }

    public void generatePosDLowResCode(StringBuilder sb) {
        sb.append("vec3 posD_LowRes(uint[FFRAMES_LOW_RES] eXLowRes, uint[FFRAMES_LOW_RES] eYLowRes, uint[FFRAMES_LOW_RES] eZLowRes, float h, vec2 U) {").append(LS);
        sb.append("    vec3 q = vec3(0.0);").append(LS);
        sb.append("    int fourierIndex = FFRAMES;").append(LS);
        sb.append("    for (int k=0; k<FFRAMES_LOW_RES; k++) {").append(LS);
        sb.append("        float w = 2.0;").append(LS);
        sb.append("        float an = -6.283185*float(fourierIndex)*h;").append(LS);
        sb.append("        vec2 ex = vec2(cos(an), sin(an));").append(LS);
        sb.append("        q.x += w*dot(decode8bit(eXLowRes[k],0U)*lowResScaleDecodeX + lowResXOffs - xOffs,ex)/OFS;").append(LS);
        sb.append("        q.y += w*dot(decode8bit(eYLowRes[k],0U)*lowResScaleDecodeY + lowResYOffs - yOffs,ex)/OFS;").append(LS);
        sb.append("        q.z += w*dot(decode8bit(eZLowRes[k],0U)*lowResScaleDecodeZ + lowResZOffs - zOffs,ex)/OFS;").append(LS);
        sb.append("        fourierIndex++;").append(LS);
        sb.append(LS);
        sb.append("        w = fourierIndex < (FFRAMES + FFRAMES_LOW_RES*2) - 1 ? 1.0 : 2.0;").append(LS);
        sb.append("        an = -6.283185*float(fourierIndex)*h;").append(LS);
        sb.append("        ex = vec2(cos(an), sin(an));").append(LS);
        sb.append("        q.x += w*dot(decode8bit(eXLowRes[k],16U)*lowResScaleDecodeX + lowResXOffs - xOffs,ex)/OFS;").append(LS);
        sb.append("        q.y += w*dot(decode8bit(eYLowRes[k],16U)*lowResScaleDecodeY + lowResYOffs - yOffs,ex)/OFS;").append(LS);
        sb.append("        q.z += w*dot(decode8bit(eZLowRes[k],16U)*lowResScaleDecodeZ + lowResZOffs - zOffs,ex)/OFS;").append(LS);
        sb.append("        fourierIndex++;").append(LS);
        sb.append("    }").append(LS);
        sb.append("    return q;").append(LS);
        sb.append("}").append(LS);
        sb.append(LS);
    }

    public void generateCodeMainStart(StringBuilder sb, boolean useLowResolution, int hiResFrames,int lowResFrames) {
        sb.append("void mainImage(out vec4 C, vec2 U) {").append(LS);
        sb.append(LS);
        sb.append("    vec3 pos = vec3(0.0);").append(LS);
        //hi res empty
        sb.append("    uint[FFRAMES] emptyHiRes = uint[FFRAMES] (");
        for (int i = 0; i < hiResFrames;  i++) {
            sb.append("0x00000000U");
            if (i < hiResFrames - 1)
                sb.append(",");
        }
        sb.append(");").append(LS);
        //low res empty
        if (useLowResolution) {
            sb.append("    uint[FFRAMES_LOW_RES] emptyLoRes = uint[FFRAMES_LOW_RES] (");
            for (int i = 0; i < lowResFrames/2;  i++) {
                sb.append("0x00000000U");
                if (i < lowResFrames/2 - 1)
                    sb.append(",");
            }
            sb.append(");").append(LS);
        }
        sb.append("    float h = mod(floor(T*8.), ")
                .append(fourierFrames)
                .append(".) / ")
                .append(fourierFrames)
                .append(".;")
                .append(LS);
        sb.append(LS);
    }

    public void generateCodeHiResData(StringBuilder encBuffer,
                                      FourierJoint fourierJoint,
                                      int hiResJointLength,
                                      Map<Integer, FixedJoint> fixedJoints) {

        StringBuilder sbeX = new StringBuilder();
        sbeX.append("(");
        StringBuilder sbeY = new StringBuilder();
        sbeY.append("(");
        StringBuilder sbeZ = new StringBuilder();
        sbeZ.append("(");
        int j = 0;
        for (FourierFrame fourierFrame : fourierJoint.getFourierFrames()) {
            if (j >= hiResJointLength)
                break;
            int x1 = fourierFrame.getFourierX1().intValue() + fourierBounds.getXOffs();
            int x2 = fourierFrame.getFourierX2().intValue() + fourierBounds.getXOffs();
            int y1 = fourierFrame.getFourierY1().intValue() + fourierBounds.getYOffs();
            int y2 = fourierFrame.getFourierY2().intValue() + fourierBounds.getYOffs();
            int z1 = fourierFrame.getFourierZ1().intValue() + fourierBounds.getZOffs();
            int z2 = fourierFrame.getFourierZ2().intValue() + fourierBounds.getZOffs();
            sbeX.append("0x").append(Integer.toHexString(BinaryHelper.encode(x1, x2))).append("U");
            sbeY.append("0x").append(Integer.toHexString(BinaryHelper.encode(y1, y2))).append("U");
            sbeZ.append("0x").append(Integer.toHexString(BinaryHelper.encode(z1, z2))).append("U");
            if (j < hiResJointLength - 1) {
                sbeX.append(",");
                sbeY.append(",");
                sbeZ.append(",");
            }
            j++;
        }
        sbeX.append(")");
        sbeY.append(")");
        sbeZ.append(")");

        FixedJoint fixedJoint = fixedJoints.get(fourierJoint.getJointId());
        encBuffer.append(generateJointData("posD", "emptyHiRes", "FFRAMES", sbeX, sbeY, sbeZ, fixedJoint));
    }

    public void generateCodeLowResData(StringBuilder encBuffer,
                                       FourierJoint fourierJoint,
                                       int lowResJointLength,
                                       Map<Integer, FixedJoint> fixedJoints) {

        StringBuilder sbeX = new StringBuilder();
        sbeX.append("(");
        StringBuilder sbeY = new StringBuilder();
        sbeY.append("(");
        StringBuilder sbeZ = new StringBuilder();
        sbeZ.append("(");
        for (int k = 0; k < lowResJointLength; k++) {
            if (k % 2 == 1) {
                FourierFrame fourierFrame1 = fourierJoint.getFourierFrames().get(k - 1 + cutoff);
                FourierFrame fourierFrame2 = fourierJoint.getFourierFrames().get(k + cutoff);
                int f1x1 = (int) ((fourierFrame1.getFourierX1() + fourierBounds.getXOffs() - lowResBounds.getBounds()[MINX]) * lowResBounds.getLowResScaleEncodeX());
                int f1x2 = (int) ((fourierFrame1.getFourierX2() + fourierBounds.getXOffs() - lowResBounds.getBounds()[MINX]) * lowResBounds.getLowResScaleEncodeX());
                int f1y1 = (int) ((fourierFrame1.getFourierY1() + fourierBounds.getYOffs() - lowResBounds.getBounds()[MINY]) * lowResBounds.getLowResScaleEncodeY());
                int f1y2 = (int) ((fourierFrame1.getFourierY2() + fourierBounds.getYOffs() - lowResBounds.getBounds()[MINY]) * lowResBounds.getLowResScaleEncodeY());
                int f1z1 = (int) ((fourierFrame1.getFourierZ1() + fourierBounds.getZOffs() - lowResBounds.getBounds()[MINZ]) * lowResBounds.getLowResScaleEncodeZ());
                int f1z2 = (int) ((fourierFrame1.getFourierZ2() + fourierBounds.getZOffs() - lowResBounds.getBounds()[MINZ]) * lowResBounds.getLowResScaleEncodeZ());
                int f2x1 = (int) ((fourierFrame2.getFourierX1() + fourierBounds.getXOffs() - lowResBounds.getBounds()[MINX]) * lowResBounds.getLowResScaleEncodeX());
                int f2x2 = (int) ((fourierFrame2.getFourierX2() + fourierBounds.getXOffs() - lowResBounds.getBounds()[MINX]) * lowResBounds.getLowResScaleEncodeX());
                int f2y1 = (int) ((fourierFrame2.getFourierY1() + fourierBounds.getYOffs() - lowResBounds.getBounds()[MINY]) * lowResBounds.getLowResScaleEncodeY());
                int f2y2 = (int) ((fourierFrame2.getFourierY2() + fourierBounds.getYOffs() - lowResBounds.getBounds()[MINY]) * lowResBounds.getLowResScaleEncodeY());
                int f2z1 = (int) ((fourierFrame2.getFourierZ1() + fourierBounds.getZOffs() - lowResBounds.getBounds()[MINZ]) * lowResBounds.getLowResScaleEncodeZ());
                int f2z2 = (int) ((fourierFrame2.getFourierZ2() + fourierBounds.getZOffs() - lowResBounds.getBounds()[MINZ]) * lowResBounds.getLowResScaleEncodeZ());
                sbeX.append("0x").append(Integer.toHexString(BinaryHelper.encode(f1x1, f1x2, f2x1, f2x2))).append("U");
                sbeY.append("0x").append(Integer.toHexString(BinaryHelper.encode(f1y1, f1y2, f2y1, f2y2))).append("U");
                sbeZ.append("0x").append(Integer.toHexString(BinaryHelper.encode(f1z1, f1z2, f2z1, f2z2))).append("U");
                if (k < lowResJointLength - 1) {
                    sbeX.append(",");
                    sbeY.append(",");
                    sbeZ.append(",");
                }
            }
        }
        sbeX.append(")");
        sbeY.append(")");
        sbeZ.append(")");

        FixedJoint fixedJoint = fixedJoints.get(fourierJoint.getJointId());
        encBuffer.append(generateJointData("posD_LowRes",  "emptyLoRes", "FFRAMES_LOW_RES", sbeX, sbeY, sbeZ, fixedJoint));
    }

    private StringBuilder generateJointData(String posDFunctionName,
                                            String emptyArrayName,
                                            String sFrames,
                                            StringBuilder sbeX,
                                            StringBuilder sbeY,
                                            StringBuilder sbeZ,
                                            FixedJoint fixedJoint) {

        StringBuilder sbr = new StringBuilder();
        if (fixedJoint != null && fixedJoint.isXFixed() && fixedJoint.isYFixed() && fixedJoint.isZFixed())
            return sbr;
        if (fixedJoint != null && fixedJoint.isXFixed()) {
            //sbr.append("        pos = ").append(posDFunctionName).append("(emptyHiRes,").append(LS);
            sbr.append("        pos = ").append(posDFunctionName).append("(").append(emptyArrayName).append(",").append(LS);
        } else {
            sbr.append("        pos = ").append(posDFunctionName).append("(uint[").append(sFrames).append("] ").append(sbeX).append(",").append(LS);
        }
        if (fixedJoint != null && fixedJoint.isYFixed()) {
            //sbr.append("                   emptyHiRes,").append(LS);
            sbr.append("                   ").append(emptyArrayName).append(",").append(LS);
        } else {
            sbr.append("                   uint[").append(sFrames).append("] ").append(sbeY).append(",").append(LS);
        }
        if (fixedJoint != null && fixedJoint.isZFixed()) {
            //sbr.append("                   emptyHiRes,h,U);").append(LS);
            sbr.append("                   ").append(emptyArrayName).append(",h,U);").append(LS);
        } else {
            sbr.append("                   uint[").append(sFrames).append("] ").append(sbeZ).append(",h,U);").append(LS);
        }
        return sbr;
    }

    public StringBuilder generatePosData(FixedJoint fixedJoint) {
        StringBuilder sbr = new StringBuilder();
        if (fixedJoint != null && fixedJoint.isXFixed()) {
            sbr.append("        pos.x = ").append(fixedJoint.getXPos()).append(";").append(LS);
        }
        if (fixedJoint != null && fixedJoint.isYFixed()) {
            sbr.append("        pos.y = ").append(fixedJoint.getYPos()).append(";").append(LS);
        }
        if (fixedJoint != null && fixedJoint.isZFixed()) {
            sbr.append("        pos.z = ").append(fixedJoint.getZPos()).append(";").append(LS);
        }
        return sbr;
    }

    public void generateCodeMainEnd(StringBuilder sb, boolean useLowResolution) {
        sb.append("    if (iFrame>0) {").append(LS);
        sb.append("        pos = mix(pos,texelFetch(iChannel0, ivec2(U),0).xyz,0.7);").append(LS);
        sb.append("    }").append(LS);
        sb.append("    C = vec4(pos,1.0);").append(LS);
        sb.append("}").append(LS);
    }

    public void generateDebug(StringBuilder sb) {
        sb.append(LS);
        sb.append("/* Fourier Generation DEBUG Information").append(LS);
        sb.append("  minX: ").append(fourierBounds.getBounds()[MINX]).append(LS);
        sb.append("  maxX: ").append(fourierBounds.getBounds()[MAXX]).append(LS);
        if (fourierBounds.getBounds()[MAXX] - fourierBounds.getBounds()[MINX] > MAX_16_BIT) {
            sb.append("  WARNING: Try reducing fourier scale value. X differential is greater than ")
                    .append(MAX_16_BIT)
                    .append(LS);
        }
        sb.append("  minY: ").append(fourierBounds.getBounds()[MINY]).append(LS);
        sb.append("  maxY: ").append(fourierBounds.getBounds()[MAXY]).append(LS);
        if (fourierBounds.getBounds()[MAXY] - fourierBounds.getBounds()[MINY] > MAX_16_BIT) {
            sb.append("  WARNING: Try reducing fourier scale value. Y differential is greater than ")
                    .append(MAX_16_BIT)
                    .append(LS);
        }
        sb.append("  minZ: ").append(fourierBounds.getBounds()[MINZ]).append(LS);
        sb.append("  maxZ: ").append(fourierBounds.getBounds()[MAXZ]).append(LS);
        if (fourierBounds.getBounds()[MAXZ] - fourierBounds.getBounds()[MINZ] > MAX_16_BIT) {
            sb.append("  WARNING: Try reducing fourier scale value. Z differential is greater than ")
                    .append(MAX_16_BIT)
                    .append(LS);
        }
        sb.append("  max low res x: ").append(lowResBounds.getMaxLowResDevX()).append(LS);
        sb.append("  max low res y: ").append(lowResBounds.getMaxLowResDevY()).append(LS);
        sb.append("  max low res z: ").append(lowResBounds.getMaxLowResDevZ()).append(LS);
        sb.append("  lowResMinX: ").append(lowResBounds.getBounds()[MINX]).append(LS);
        sb.append("  lowResMaxX: ").append(lowResBounds.getBounds()[MAXX]).append(LS);
        sb.append("  lowResMinY: ").append(lowResBounds.getBounds()[MINY]).append(LS);
        sb.append("  lowResMaxY: ").append(lowResBounds.getBounds()[MAXY]).append(LS);
        sb.append("  lowResMinZ: ").append(lowResBounds.getBounds()[MINZ]).append(LS);
        sb.append("  lowResMaxZ: ").append(lowResBounds.getBounds()[MAXZ]).append(LS);
        sb.append("*/").append(LS);
    }
 }
