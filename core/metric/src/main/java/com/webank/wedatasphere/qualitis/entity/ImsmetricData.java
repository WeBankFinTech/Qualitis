package com.webank.wedatasphere.qualitis.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author v_wenxuanzhang
 */
//@Entity
//@Table(name = "qualitis_imsmetric_data_new")
//@IdClass(ImsmetricPrimaryKey.class)
public class ImsmetricData {

    @Id
    @Column(name = "metric_id")
    private Long metricId;

    @Column(name = "metric_value")
    private BigDecimal y;

    @Id
    @Column(name = "data_date")
    private Long ds;

    @Column(name = "create_time", updatable = false)
    private Date createTime;
    @Column(name = "update_time")
    private Date updateTime;
    @Column(name = "datasource_user")
    private String datasourceUser;
    @Column(name = "datasource_type", columnDefinition = "TINYINT(2)")
    private Integer datasourceType;

    public ImsmetricData(Long imsMetricId, BigDecimal imsMetricValue) {
        this.metricId = imsMetricId;
        this.y = imsMetricValue;
    }

    public ImsmetricData() {
        // do nothing
    }

    public Long getMetricId() {
        return metricId;
    }

    public void setMetricId(Long metricId) {
        this.metricId = metricId;
    }

    public BigDecimal getY() {
        return y;
    }

    public void setY(BigDecimal y) {
        this.y = y;
    }

    public Long getDs() {
        return ds;
    }

    public void setDs(Long ds) {
        this.ds = ds;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDatasourceUser() {
        return datasourceUser;
    }

    public void setDatasourceUser(String datasourceUser) {
        this.datasourceUser = datasourceUser;
    }

    public Integer getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(Integer datasourceType) {
        this.datasourceType = datasourceType;
    }
}
