package com.webank.wedatasphere.qualitis.scheduled.request.checker;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2022-11-02 14:17
 * @description
 */
public class RmbSendJsonParameterChecker extends AbstractJsonParameterChecker {

    @Override
    public void check(String contentJson) throws UnExpectedRequestException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> contentMap = super.convertMap(contentJson);
        try {
            String rmbMsg = String.valueOf(contentMap.getOrDefault("rmb.message", "")).replace('\'', '"');
            objectMapper.readValue(rmbMsg, Map.class);
        } catch (IOException e) {
            throw new UnExpectedRequestException("error json format: rmb.message");
        }
        checkMapValue(contentMap, "user.to.proxy", 20, true);
        checkMapValue(contentMap, "rmb.messageType", 10, true);
        checkMapValue(contentMap, "rmb.targetDcn", 30, true);
        checkMapValue(contentMap, "rmb.serviceId", 20, true);
        checkMapValue(contentMap, "rmb.environment", 20, true);
        checkMapValue(contentMap, "rmb.message", 250, true);
    }
}
