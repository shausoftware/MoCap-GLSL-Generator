package com.shau.mocap.parser;

public class DataIndex<T> {

    private T generatedObject;
    private int idx;
    private boolean continueParsing;

    public DataIndex(T generatedObject, int idx, boolean continueParsing) {
        this.generatedObject = generatedObject;
        this.idx = idx;
        this.continueParsing = continueParsing;
    }

    public T getGeneratedObject() {
        return generatedObject;
    }

    public int getIdx() {
        return idx;
    }

    public boolean isContinueParsing() {
        return continueParsing;
    }
}
