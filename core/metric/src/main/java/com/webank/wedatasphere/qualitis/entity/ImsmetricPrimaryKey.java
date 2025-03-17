package com.webank.wedatasphere.qualitis.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author v_wenxuanzhang
 */
public class ImsmetricPrimaryKey implements Serializable {
    private Long metricId;
    private Long ds;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImsmetricPrimaryKey that = (ImsmetricPrimaryKey) o;
        return metricId.equals(that.metricId) &&
                ds.equals(that.ds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metricId, ds);
    }

    public Long getMetricId() {
        return metricId;
    }

    public void setMetricId(Long metricId) {
        this.metricId = metricId;
    }

    public Long getDs() {
        return ds;
    }

    public void setDs(Long ds) {
        this.ds = ds;
    }
}
