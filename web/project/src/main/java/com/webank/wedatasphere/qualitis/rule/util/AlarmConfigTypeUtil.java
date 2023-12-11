package com.webank.wedatasphere.qualitis.rule.util;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.request.AbstractCommonAlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.AlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.CustomAlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.FileAlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.response.AlarmConfigResponse;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public class AlarmConfigTypeUtil {

    public static void checkAlarmConfigType(AbstractCommonAlarmConfigRequest abstractCommonAlarmConfigRequest) throws UnExpectedRequestException {
        if (abstractCommonAlarmConfigRequest instanceof CustomAlarmConfigRequest) {
            CustomAlarmConfigRequest.checkRequest((CustomAlarmConfigRequest) abstractCommonAlarmConfigRequest);
        } else if (abstractCommonAlarmConfigRequest instanceof AlarmConfigRequest) {
            AlarmConfigRequest.checkRequest((AlarmConfigRequest) abstractCommonAlarmConfigRequest);
        } else if (abstractCommonAlarmConfigRequest instanceof FileAlarmConfigRequest) {
            FileAlarmConfigRequest.checkRequest((FileAlarmConfigRequest) abstractCommonAlarmConfigRequest);
        }
    }

    public static List<CustomAlarmConfigRequest> changeToCustomAlarmConfigRequest(List<AbstractCommonAlarmConfigRequest> list) {
        List<CustomAlarmConfigRequest> request = Lists.newArrayList();
        list.forEach(item -> {
            if (item instanceof CustomAlarmConfigRequest) {
                request.add((CustomAlarmConfigRequest) item);
            }
        });
        return request;
    }

    public static List<FileAlarmConfigRequest> changeToFileAlarmConfigRequest(List<AbstractCommonAlarmConfigRequest> list) {
        List<FileAlarmConfigRequest> request = Lists.newArrayList();
        list.forEach(item -> {
            if (item instanceof FileAlarmConfigRequest) {
                request.add((FileAlarmConfigRequest) item);
            }
        });
        return request;
    }

    public static List<AlarmConfigRequest> changeToAlarmConfigRequest(List<AbstractCommonAlarmConfigRequest> list) {
        List<AlarmConfigRequest> request = Lists.newArrayList();
        list.forEach(item -> {
            if (item instanceof AlarmConfigRequest) {
                request.add((AlarmConfigRequest) item);
            }
        });
        return request;
    }


    public static AlarmConfigResponse checkAlarmConfigResponse(AbstractCommonAlarmConfigRequest commonAlarmConfigRequest) {
        if (commonAlarmConfigRequest instanceof AlarmConfigResponse) {
            return ((AlarmConfigResponse) commonAlarmConfigRequest);
        }
        return null;
    }

}
