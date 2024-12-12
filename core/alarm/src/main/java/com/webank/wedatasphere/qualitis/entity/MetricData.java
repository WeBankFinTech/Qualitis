package com.webank.wedatasphere.qualitis.entity;

/**
 * @author allenzhou@webank.com
 * @date 2021/4/27 15:05
 */
public class MetricData {
    private String subsystemId;
    private String interfaceName;
    private String attrGroup;
    private String attrName;
    private String collectTimestamp;
    private String metricValue;
    private String hostIp;

    public MetricData(String subsystemId, String interfaceName, String attrGroup, String attrName, String collectTime, String metricValue,
        String hostIp) {
        this.subsystemId = subsystemId;
        this.interfaceName = interfaceName;
        this.attrGroup = attrGroup;
        this.attrName = attrName;
        this.collectTimestamp = collectTime;
        this.metricValue = metricValue;
        this.hostIp = hostIp;
    }

    public MetricData() {

    }

    public String getSubsystemId() {
        return subsystemId;
    }

    public void setSubsystemId(String subsystemId) {
        this.subsystemId = subsystemId;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getAttrGroup() {
        return attrGroup;
    }

    public void setAttrGroup(String attrGroup) {
        this.attrGroup = attrGroup;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getCollectTimestamp() {
        return collectTimestamp;
    }

    public void setCollectTimestamp(String collectTimestamp) {
        this.collectTimestamp = collectTimestamp;
    }

    public String getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(String metricValue) {
        this.metricValue = metricValue;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }
}
