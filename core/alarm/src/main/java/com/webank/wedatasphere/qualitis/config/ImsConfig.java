package com.webank.wedatasphere.qualitis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author allenzhou
 */
@Configuration
public class ImsConfig {
    @Value("${alarm.ims.url}")
    private String url;
    @Value("${alarm.ims.send_alarm_path}")
    private String sendAlarmPath;
    @Value("${alarm.ims.send_report_path}")
    private String sendReportPath;
    @Value("${alarm.ims.full_url_abnormal_data_record}")
    private String fullUrlAbnormalDataRecord;
    @Value("${alarm.ims.system_id}")
    private String systemId;
    @Value("${alarm.ims.alert_way}")
    private String alertWay;
    @Value("${alarm.ims.title_prefix}")
    private String titlePrefix;
    @Value("${alarm.ims.new_title_prefix}")
    private String newTitlePrefix;
    @Value("${alarm.ims.userAuthKey}")
    private String userAuthKey;
    /**
     * 运维人员
     */
    @Value("${alarm.ims.receiver.fail}")
    private String failReceiver;
    @Value("${alarm.ims.receiver.out_threshold}")
    private String outThresholdReceiver;
    /**
     * 任务运行时长超长告警运维人员
     */
    @Value("${alarm.ims.receiver.task_time_out}")
    private String taskTimeOutReceiver;
    /**
     * 任务运行时长超长告警标题
     */
    @Value("${alarm.ims.task_time_out.alarm_title}")
    private String taskTimeOutAlarmTitle;

    @Value("${alarm.ims.new_title_succeed_prefix}")
    private String newTitleSucceedPrefix;

    public ImsConfig() {
        // 默认构造函数
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSendAlarmPath() {
        return sendAlarmPath;
    }

    public void setSendAlarmPath(String sendAlarmPath) {
        this.sendAlarmPath = sendAlarmPath;
    }

    public String getSendReportPath() {
        return sendReportPath;
    }

    public void setSendReportPath(String sendReportPath) {
        this.sendReportPath = sendReportPath;
    }

    public String getFullUrlAbnormalDataRecord() {
        return fullUrlAbnormalDataRecord;
    }

    public void setFullUrlAbnormalDataRecord(String fullUrlAbnormalDataRecord) {
        this.fullUrlAbnormalDataRecord = fullUrlAbnormalDataRecord;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getAlertWay() {
        return alertWay;
    }

    public void setAlertWay(String alertWay) {
        this.alertWay = alertWay;
    }

    public String getTitlePrefix() {
        return titlePrefix;
    }

    public void setTitlePrefix(String titlePrefix) {
        this.titlePrefix = titlePrefix;
    }

    public String getNewTitlePrefix() {
        return newTitlePrefix;
    }

    public void setNewTitlePrefix(String newTitlePrefix) {
        this.newTitlePrefix = newTitlePrefix;
    }

    public String getFailReceiver() {
        return failReceiver;
    }

    public void setFailReceiver(String failReceiver) {
        this.failReceiver = failReceiver;
    }

    public String getOutThresholdReceiver() {
        return outThresholdReceiver;
    }

    public void setOutThresholdReceiver(String outThresholdReceiver) {
        this.outThresholdReceiver = outThresholdReceiver;
    }

    public String getTaskTimeOutReceiver() {
        return taskTimeOutReceiver;
    }

    public void setTaskTimeOutReceiver(String taskTimeOutReceiver) {
        this.taskTimeOutReceiver = taskTimeOutReceiver;
    }

    public String getTaskTimeOutAlarmTitle() {
        return taskTimeOutAlarmTitle;
    }

    public void setTaskTimeOutAlarmTitle(String taskTimeOutAlarmTitle) {
        this.taskTimeOutAlarmTitle = taskTimeOutAlarmTitle;
    }

    public String getUserAuthKey() {
        return userAuthKey;
    }

    public void setUserAuthKey(String userAuthKey) {
        this.userAuthKey = userAuthKey;
    }

    public String getNewTitleSucceedPrefix() {
        return newTitleSucceedPrefix;
    }

    public void setNewTitleSucceedPrefix(String newTitleSucceedPrefix) {
        this.newTitleSucceedPrefix = newTitleSucceedPrefix;
    }
}
