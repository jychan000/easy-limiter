package com.jychan.tools.limiter.base;

/**
 * Created by chenjinying on 2017/7/27.
 * mail: 415683089@qq.com
 */
public class LimiterKvUtils {

    public static String toString(LimiterKV[] keyValues) {
        if (keyValues == null || keyValues.length <= 0) {
            return null;
        }
        final StringBuilder sb = new StringBuilder("[");
        final StringBuilder sbTmp = new StringBuilder();
        for (LimiterKV keyValue : keyValues) {
            sbTmp.append(",").append(toString(keyValue));
        }
        sb.append(sbTmp.length() > 0 ? sbTmp.substring(1) : "");
        sb.append("]");
        return sb.toString();
    }

    public static String toString(LimiterKV keyValue) {
        if (keyValue == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder("LimiterKV{");
        sb.append("key=").append(keyValue.key());
        sb.append(", valString=").append(keyValue.valString());
        sb.append(", valInt=").append(keyValue.valInt());
        sb.append(", valDouble=").append(keyValue.valDouble());
        sb.append(", valClass=").append(keyValue.valClass());
        sb.append(", type=");
        switch (keyValue.type()) {
            case STRING: sb.append("type=STRING"); break;
            case INT:    sb.append("type=INT");    break;
            case DOUBLE: sb.append("type=DOUBLE"); break;
            case CLASS:  sb.append("type=CLASS");  break;
        }
        sb.append("}");
        return sb.toString();
    }


    public static String getValString(LimiterKV[] keyValues, String key, String defValue) {
        return getVal(keyValues, key, defValue, LimiterKvType.STRING);
    }

    public static int getValInt(LimiterKV[] keyValues, String key, int defValue) {
        return getVal(keyValues, key, defValue, LimiterKvType.INT);
    }

    public static double getValDouble(LimiterKV[] keyValues, String key, double defValue) {
        return getVal(keyValues, key, defValue, LimiterKvType.DOUBLE);
    }

    public static Class getValClass(LimiterKV[] keyValues, String key, Class defValue) {
        return getVal(keyValues, key, defValue, LimiterKvType.CLASS);
    }

    private static <T> T getVal(LimiterKV[] keyValues, String key, T defValue, LimiterKvType type) {
        if (keyValues == null || key == null || "".equals(key)) {
            return defValue;
        }
        for (LimiterKV keyValue : keyValues) {
            if(key.equals(keyValue.key()) && type == keyValue.type()) {
                try {
                    switch (type) {
                        case STRING: return (T) keyValue.valString();
                        case INT:    return (T) (Integer) keyValue.valInt();
                        case DOUBLE: return (T) (Double) keyValue.valDouble();
                        case CLASS:  return (T) keyValue.valClass();
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return defValue;
    }
}