package com.jychan.tools.limiter.base;

import java.lang.annotation.Annotation;

/**
 * Created by chenjinying on 2018/3/12.
 * mail: 415683089@qq.com
 */
public class LimiterKVObj implements LimiterKV {

    private String key;

    private String valString;
    private int valInt;
    private double valDouble;
    private Class valClass;

    private LimiterKvType type;

    public LimiterKVObj(String key, String val) {
        this.key = key;
        this.valString = val;
        this.type = LimiterKvType.STRING;
    }
    public LimiterKVObj(String key, int val) {
        this.key = key;
        this.valInt = val;
        this.type = LimiterKvType.INT;
    }
    public LimiterKVObj(String key, double val) {
        this.key = key;
        this.valDouble = val;
        this.type = LimiterKvType.DOUBLE;
    }
    public LimiterKVObj(String key, Class val) {
        this.key = key;
        this.valClass = val;
        this.type = LimiterKvType.CLASS;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public String valString() {
        return valString;
    }

    @Override
    public int valInt() {
        return valInt;
    }

    @Override
    public double valDouble() {
        return valDouble;
    }

    @Override
    public Class valClass() {
        return valClass;
    }

    @Override
    public LimiterKvType type() {
        return type;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
