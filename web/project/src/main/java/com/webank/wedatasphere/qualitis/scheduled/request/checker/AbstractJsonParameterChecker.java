package com.webank.wedatasphere.qualitis.scheduled.request.checker;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author v_minminghe@webank.com
 * @date 2022-11-02 11:20
 * @description
 */
public abstract class AbstractJsonParameterChecker {

    private final ObjectMapper objectMapper = new ObjectMapper();

    protected void checkMapValue(Map<String, Object> contentMap, String key, int maxLength, boolean isRequired) throws UnExpectedRequestException {
        boolean isEmpty = isRequired && (!contentMap.containsKey(key) || Objects.isNull(contentMap.get(key)));
        if (isEmpty) {
            throw new UnExpectedRequestException("Parameter must not be empty: " + key);
        }
        if (contentMap.containsKey(key)) {
            Object value = contentMap.get(key);
            if (String.valueOf(value).length() > maxLength) {
                String errorMsg = String.format("The maximum length of {%s} is exceeded: %d", key, maxLength);
                throw new UnExpectedRequestException(errorMsg);
            }
        }
    }

    protected Map<String, Object> convertMap(String contentJson) throws UnExpectedRequestException {
        try {
            return objectMapper.readValue(contentJson, Map.class);
        } catch (IOException e) {
            throw new UnExpectedRequestException("error json format");
        }
    }

    /**
     * Checking the format of json by the signal type
     * @param contentJson
     * @throws UnExpectedRequestException
     */
    public abstract void check(String contentJson) throws UnExpectedRequestException;
}
