package com.webank.wedatasphere.qualitis.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.Date;

/**
 * @author allenzhou@webank.com
 * @date 2021/10/7 12:49
 */
public class AbnormalDataRecordPrimaryKey implements Serializable {
    private Long ruleId;
    private String dbName;
    private String tableName;
    private String recordDate;

    public AbnormalDataRecordPrimaryKey() {
        // Do nothing.
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
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

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbnormalDataRecordPrimaryKey that = (AbnormalDataRecordPrimaryKey) o;
        return Objects.equals(ruleId, that.ruleId) &&
            Objects.equals(dbName, that.dbName) &&
            Objects.equals(tableName, that.tableName) &&
            Objects.equals(recordDate, that.recordDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleId, dbName, tableName, recordDate);
    }
}
