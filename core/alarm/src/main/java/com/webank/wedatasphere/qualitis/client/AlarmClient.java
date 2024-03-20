package com.webank.wedatasphere.qualitis.client;

import com.webank.wedatasphere.qualitis.config.ImsConfig;
import com.webank.wedatasphere.qualitis.entity.ReportBatchInfo;
import java.util.List;
import java.util.Map;

/**
 * @author allenzhou
 */
public interface AlarmClient {
    /**
     * 发送告警
     * @param receiver
     * @param alertInfo
     * @param alertLevel
     * @param alertTitle
     * @return
     */
    void sendAlarm(String receiver, String alertTitle, String alertInfo, String alertLevel);

    /**
     * Send new alarm.
     * @param requestList
     */
    void sendNewAlarm(List<Map<String, Object>> requestList);

    /**
     * 上报指标值 或 异常值
     * @param reportBatchInfo
     */
    void report(ReportBatchInfo reportBatchInfo);

    /**
     * Send Abnormal data record.
     * @param imsConfig
     * @param data
     */
    void sendAbnormalDataRecordAlarm(ImsConfig imsConfig, List<Map<String, Object>> data);
}
