package com.webank.wedatasphere.qualitis.entity;

import java.util.Objects;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 * @author allenzhou@webank.com
 * @date 2021/10/5 11:10
 */
@Entity
@Table(name = "qualitis_abnormal_data_record_info")
@IdClass(AbnormalDataRecordPrimaryKey.class)
public class AbnormalDataRecordInfo {
    @Id
    @Column(name = "rule_id")
    private Long ruleId;
    @Column(name = "rule_name")
    private String ruleName;
    @Column(name = "rule_detail")
    private String ruleDetail;
    @Column(name = "datasource")
    private String datasource;
    @Id
    @Column(name = "db_name")
    private String dbName;
    @Id
    @Column(name = "table_name")
    private String tableName;
    @Column(name = "dept")
    private String departmentName;
    @Column(name = "sub_system_id")
    private Integer subSystemId;
    @Column(name = "execute_num")
    private Integer executeNum;
    @Column(name = "event_num")
    private Integer eventNum;
    @Id
    @Column(name = "record_date")
    private String recordDate;
    @Column(name = "record_time")
    private String recordTime;

    public AbnormalDataRecordInfo() {
        // Do nothing.
    }

    public AbnormalDataRecordInfo(Long ruleId, String ruleName, String datasourceType, String dbName, String tableName, String departmentName, Integer subSystemId, int execNum, int alarmNum) {
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.datasource = datasourceType;
        this.dbName = dbName;
        this.tableName = tableName;
        this.departmentName = departmentName;
        this.subSystemId = subSystemId;
        this.executeNum = execNum;
        this.eventNum = alarmNum;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleDetail() {
        return ruleDetail;
    }

    public void setRuleDetail(String ruleDetail) {
        this.ruleDetail = ruleDetail;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getSubSystemId() {
        return subSystemId;
    }

    public void setSubSystemId(Integer subSystemId) {
        this.subSystemId = subSystemId;
    }

    public Integer getExecuteNum() {
        return executeNum;
    }

    public void setExecuteNum(Integer executeNum) {
        this.executeNum = executeNum;
    }

    public Integer getEventNum() {
        return eventNum;
    }

    public void setEventNum(Integer eventNum) {
        this.eventNum = eventNum;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbnormalDataRecordInfo that = (AbnormalDataRecordInfo) o;
        return Objects.equals(ruleId, that.ruleId) &&
            Objects.equals(ruleName, that.ruleName) &&
            Objects.equals(ruleDetail, that.ruleDetail) &&
            Objects.equals(datasource, that.datasource) &&
            Objects.equals(dbName, that.dbName) &&
            Objects.equals(tableName, that.tableName) &&
            Objects.equals(departmentName, that.departmentName) &&
            Objects.equals(subSystemId, that.subSystemId) &&
            Objects.equals(executeNum, that.executeNum) &&
            Objects.equals(eventNum, that.eventNum) &&
            Objects.equals(recordDate, that.recordDate) &&
            Objects.equals(recordTime, that.recordTime);
    }

    @Override
    public int hashCode() {
        return Objects
            .hash(ruleId, ruleName, ruleDetail, datasource, dbName, tableName, departmentName, subSystemId, executeNum, eventNum, recordDate,
                recordTime);
    }

    @Override
    public String toString() {
        return "AbnormalDataRecordInfo{" +
            "ruleId=" + ruleId +
            ", ruleName='" + ruleName + '\'' +
            ", ruleDetail='" + ruleDetail + '\'' +
            ", datasource='" + datasource + '\'' +
            ", dbName='" + dbName + '\'' +
            ", tableName='" + tableName + '\'' +
            ", departmentName='" + departmentName + '\'' +
            ", subSystemId=" + subSystemId +
            ", executeNum=" + executeNum +
            ", eventNum=" + eventNum +
            ", recordDate=" + recordDate +
            ", recordTime=" + recordTime +
            '}';
    }
}
