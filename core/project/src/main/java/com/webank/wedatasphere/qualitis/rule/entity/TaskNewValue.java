package com.webank.wedatasphere.qualitis.rule.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author v_gaojiedeng@webank.com
 */
@Entity
@Table(name = "qualitis_task_new_value")
public class TaskNewValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 规则Id
     */
    @Column(name = "rule_id")
    private Long ruleId;

    /**
     * 新值数据集
     */
    @Column(name = "result_value",columnDefinition = "MEDIUMTEXT")
    private String resultValue;

    /**
     * 状态（未处理/已录入/已丢弃）
     */
    @Column(name = "status")
    private Long status;

    /**
     * 创建人
     */
    @Column(name = "create_user")
    private String createUser;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private String createTime;

    /**
     * 修改人
     */
    @Column(name = "modify_user")
    private String modifyUser;

    /**
     * 修改时间
     */
    @Column(name = "modify_time")
    private String modifyTime;
    /**
     * 规则版本
     */
    @Column(name = "rule_version")
    private String ruleVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getResultValue() {
        return resultValue;
    }

    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getRuleVersion() {
        return ruleVersion;
    }

    public void setRuleVersion(String ruleVersion) {
        this.ruleVersion = ruleVersion;
    }

    @Override
    public String toString() {
        return "TaskNewValue{" +
                "id=" + id +
                ", ruleId=" + ruleId +
                ", resultValue='" + resultValue + '\'' +
                ", status=" + status +
                ", createUser='" + createUser + '\'' +
                ", createTime='" + createTime + '\'' +
                ", modifyUser='" + modifyUser + '\'' +
                ", modifyTime='" + modifyTime + '\'' +
                '}';
    }
}
