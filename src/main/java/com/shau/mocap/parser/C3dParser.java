package com.shau.mocap.parser;

import com.shau.mocap.exception.ParserException;
import com.shau.mocap.parser.c3d.C3dDataParser;
import com.shau.mocap.parser.c3d.C3dValueMetadata;
import com.shau.mocap.parser.c3d.domain.C3dGroup;
import com.shau.mocap.parser.c3d.domain.C3dHeader;
import com.shau.mocap.parser.c3d.domain.C3dParameter;
import com.shau.mocap.parser.c3d.domain.C3dValue;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class C3dParser {

    private static final int BLOCK_SIZE = 512;

    public List<C3dValue> parseData(BufferedInputStream bis) throws IOException, ParserException {

        byte[] headerBlock = BinaryHelper.loadData(bis, BLOCK_SIZE);
        int parameterStartLocation = C3dDataParser.parseAndValidateHeader(headerBlock);

        //TODO skip???
        int skip = BLOCK_SIZE * (parameterStartLocation - 1) - BLOCK_SIZE;
        //bis.skip(skip);

        bis.mark(0);

        byte[] parameterMetaDataBlock = BinaryHelper.loadData(bis, 4);
        int numberOfParameterBlocks = C3dDataParser.parseNoOfParameters(parameterMetaDataBlock);

        bis.reset();

        byte[] parametersBlock = BinaryHelper.loadData(bis, numberOfParameterBlocks * BLOCK_SIZE);
        int processorType = C3dDataParser.parseProcessorType(parametersBlock);
        ByteOrder byteOrder = BinaryHelper.getEndian(processorType);

        Map<Integer, C3dGroup> groups = new HashMap<>();
        int idx = 4;
        boolean parseParameters = true;
        while (parseParameters) {
            DataIndex currentIdx = C3dDataParser.parseGroupParameterData(parametersBlock, idx, byteOrder);
            idx = currentIdx.getIdx();
            if (!currentIdx.isContinueParsing()) {
                parseParameters = false;
            } else {
                if (currentIdx.getGeneratedObject() instanceof C3dGroup) {
                    C3dGroup group = (C3dGroup) currentIdx.getGeneratedObject();
                    groups.put(group.getId(), group);
                } else if (currentIdx.getGeneratedObject() instanceof C3dParameter) {
                    C3dParameter parameter = (C3dParameter) currentIdx.getGeneratedObject();
                    groups.get(parameter.getGroupId()).addParameter(parameter);
                }
            }
        }

        C3dHeader header = C3dDataParser.parseHeader(headerBlock, byteOrder);
        C3dValueMetadata valueMetadata = new C3dValueMetadata(groups);

        int nextIdx = BLOCK_SIZE * (valueMetadata.getDataStart() - 1);
        skip = nextIdx - BLOCK_SIZE * (numberOfParameterBlocks + 1);
        //bis.skip(skip);

        byte[] dataBlock = BinaryHelper.loadData(bis, BLOCK_SIZE * valueMetadata.getBlocks());
        List<C3dValue> dataValues = C3dDataParser.parseDataValues(dataBlock, valueMetadata, byteOrder);

        return dataValues;
    }
}
