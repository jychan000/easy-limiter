package com.jychan.tools.limiter.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by chenjinying on 2017/7/19.
 * mail: 415683089@qq.com
 */
public class JsonObjectMapper {

    private static final ObjectMapper objectMapper;

    private JsonObjectMapper() {
    }

    public static final ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    static {
        objectMapper = (new ObjectMapper()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

}
