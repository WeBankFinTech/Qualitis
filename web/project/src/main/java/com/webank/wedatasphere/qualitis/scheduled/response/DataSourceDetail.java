package com.webank.wedatasphere.qualitis.scheduled.response;

import com.webank.wedatasphere.qualitis.project.response.HiveDataSourceDetail;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledFrontBackRule;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public class DataSourceDetail {

    private Long scheduledTaskFrontBackId;
    private Long ruleGroupId;
    private String ruleGroupName;
    private Integer frontOrBack;
    private Boolean tableGroup;
    private List<HiveDataSourceDetail> hiveDataSource;

    public DataSourceDetail() {
    }

    public DataSourceDetail(RuleGroup ruleGroup, List<HiveDataSourceDetail> hiveDataSource, ScheduledFrontBackRule scheduledFrontBackRule) {
        this.scheduledTaskFrontBackId = scheduledFrontBackRule.getId();
        this.ruleGroupId = ruleGroup.getId();
        this.ruleGroupName = ruleGroup.getRuleGroupName();
        this.frontOrBack = scheduledFrontBackRule.getTriggerType();
        if (CollectionUtils.isNotEmpty(ruleGroup.getRuleDataSources())) {
            this.tableGroup = true;
        } else {
            this.tableGroup = false;
        }
        this.hiveDataSource = hiveDataSource;
    }

    public Long getScheduledTaskFrontBackId() {
        return scheduledTaskFrontBackId;
    }

    public void setScheduledTaskFrontBackId(Long scheduledTaskFrontBackId) {
        this.scheduledTaskFrontBackId = scheduledTaskFrontBackId;
    }

    public Boolean getTableGroup() {
        return tableGroup;
    }

    public void setTableGroup(Boolean tableGroup) {
        this.tableGroup = tableGroup;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public String getRuleGroupName() {
        return ruleGroupName;
    }

    public void setRuleGroupName(String ruleGroupName) {
        this.ruleGroupName = ruleGroupName;
    }

    public Integer getFrontOrBack() {
        return frontOrBack;
    }

    public void setFrontOrBack(Integer frontOrBack) {
        this.frontOrBack = frontOrBack;
    }

    public List<HiveDataSourceDetail> getHiveDataSource() {
        return hiveDataSource;
    }

    public void setHiveDataSource(List<HiveDataSourceDetail> hiveDataSource) {
        this.hiveDataSource = hiveDataSource;
    }

}
