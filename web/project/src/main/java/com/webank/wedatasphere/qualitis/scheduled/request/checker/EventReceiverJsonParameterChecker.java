package com.webank.wedatasphere.qualitis.scheduled.request.checker;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2022-11-02 11:25
 * @description
 */
public class EventReceiverJsonParameterChecker extends AbstractJsonParameterChecker {

    @Override
    public void check(String contentJson) throws UnExpectedRequestException {
        Map<String, Object> contentMap = super.convertMap(contentJson);
        checkMapValue(contentMap, "user.to.proxy", 20, true);
        checkMapValue(contentMap, "msg.type", 30, true);
        checkMapValue(contentMap, "msg.receiver", 30, true);
        checkMapValue(contentMap, "msg.topic", 30, true);
        checkMapValue(contentMap, "msg.name", 30, true);
        checkMapValue(contentMap, "msg.key", 100, false);
    }
}
