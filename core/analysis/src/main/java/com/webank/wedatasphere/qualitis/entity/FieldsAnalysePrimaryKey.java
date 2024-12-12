package com.webank.wedatasphere.qualitis.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author v_wenxuanzhang
 */
public class FieldsAnalysePrimaryKey implements Serializable {
    private Long ruleId;
    private Integer dataDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldsAnalysePrimaryKey that = (FieldsAnalysePrimaryKey) o;
        return ruleId.equals(that.ruleId) &&
                dataDate.equals(that.dataDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleId, dataDate);
    }

    public Long getMetricId() {
        return ruleId;
    }

    public void setMetricId(Long metricId) {
        this.ruleId = metricId;
    }

    public Integer getDs() {
        return dataDate;
    }

    public void setDs(Integer ds) {
        this.dataDate = ds;
    }
}
