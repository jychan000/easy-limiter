package com.jychan.tools.limiter.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Created by chenjinying on 2018/3/12.
 * mail: 415683089@qq.com
 */
public class LimiterReturnUtils {

    private static Logger logger = Logger.getLogger(LimiterReturnUtils.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();
    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static Object returnRejectObject(String rejectReturnVal, Class<?> returnType) throws Exception {
        try {
            // TODO: 2018/3/10 设法在初始化的时候检查一次返回配置是否正确

            Object object = null;

            if (void.class.equals(returnType)) {
            } else if (String.class.equals(returnType)) { // 基础类型
                object = rejectReturnVal;
            } else if (short.class.equals(returnType) || Short.class.equals(returnType)) {
                object =  Short.parseShort(rejectReturnVal);
            } else if (int.class.equals(returnType) || Integer.class.equals(returnType)) {
                object =  Integer.parseInt(rejectReturnVal);
            } else if (long.class.equals(returnType) || Long.class.equals(returnType)) {
                object =  Long.parseLong(rejectReturnVal);
            } else if (float.class.equals(returnType) || Float.class.equals(returnType)) {
                object =  Float.parseFloat(rejectReturnVal);
            } else if (double.class.equals(returnType) || Double.class.equals(returnType)) {
                object =  Double.parseDouble(rejectReturnVal);
            } else if (boolean.class.equals(returnType) || Boolean.class.equals(returnType)) {
                object =  Boolean.parseBoolean(rejectReturnVal);
            } else if (byte.class.equals(returnType) || Byte.class.equals(returnType)) {
                object =  rejectReturnVal.getBytes();
            } else if (char.class.equals(returnType)) {
                object =  rejectReturnVal.charAt(0);
            } else {
                // 来到此处认为是引用类型 json -> obj
                try {
                    if(StringUtils.isNotBlank(rejectReturnVal)) {
                        object = MAPPER.readValue(rejectReturnVal, returnType);
                    }
                } catch (Exception jsone) {
                    logger.warn("[fromJson] Could not parse json -> "+rejectReturnVal+", to type -> "+returnType);
                    throw jsone;
                }
            }
            return object;

        } catch (Exception e) {
            throw new Exception("限流默认拒绝返回值无法转成对应的object，请检查限流配置，returnType=" + returnType + ", rejectReturnVal=" + rejectReturnVal + " message=" + e.getMessage(), e);
        }
    }

}
