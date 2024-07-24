package com.webank.wedatasphere.qualitis.scheduled.request.checker;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;

import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2023-04-25 9:26
 * @description
 */
public class ScheduledSignalChecker extends AbstractJsonParameterChecker {

    @Override
    public void check(String contentJson) throws UnExpectedRequestException {
        Map<String, Object> contentMap = super.convertMap(contentJson);
        checkMapValue(contentMap, "msg.sender", 30, true);
        checkMapValue(contentMap, "msg.topic", 30, true);
        checkMapValue(contentMap, "msg.name", 30, true);
        checkMapValue(contentMap, "msg.key", 30, true);
    }
}
