package com.webank.wedatasphere.dss.appconn.qualitis.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    public static Map<String, Object> readValue(String jsonBody) {
        try {
            return objectMapper.readValue(jsonBody, Map.class);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        } finally {
            return new HashMap<>();
        }
    }
}
