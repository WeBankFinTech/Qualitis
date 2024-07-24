package com.webank.wedatasphere.qualitis.scheduled.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.project.response.HiveDataSourceDetail;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledFrontBackRule;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledTask;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public class ScheduledTaskResponse {

    @JsonProperty("scheduled_task_id")
    private Long scheduledTaskId;
    @JsonProperty("schedule_system")
    private String scheduleSystem;
    @JsonProperty("cluster")
    private String cluster;
    @JsonProperty("project_name")
    private String projectName;
    @JsonProperty("work_flow")
    private String workFlow;
    @JsonProperty("task_name")
    private String taskName;
    @JsonProperty("datasource")
    private List<DataSourceDetail> datasource;
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("project_type")
    private Integer projectType;
    @JsonProperty("release_user")
    private String releaseUser;
    @JsonProperty("release_status")
    private Integer releaseStatus;

    private List<HiveDataSourceDetail> hiveDataSource;

    public ScheduledTaskResponse() {
    }

    public ScheduledTaskResponse(ScheduledTask scheduledTask, List<ScheduledFrontBackRule> dataList) {
        this.scheduledTaskId = scheduledTask.getId();
        this.scheduleSystem = scheduledTask.getDispatchingSystemType();
        this.cluster = scheduledTask.getClusterName();
        this.projectName = scheduledTask.getProjectName();
        this.workFlow = scheduledTask.getWorkFlowName();
        this.taskName = scheduledTask.getTaskName();
        this.projectId = scheduledTask.getProject().getId();
        this.projectType = scheduledTask.getProject().getProjectType();
        this.releaseUser = scheduledTask.getReleaseUser();
        this.releaseStatus = scheduledTask.getReleaseStatus();
        datasource = new ArrayList<>();

        for (ScheduledFrontBackRule scheduledFrontBackRule : dataList) {
            RuleGroup ruleGroup = scheduledFrontBackRule.getRuleGroup();
            RuleDao ruleDao = SpringContextHolder.getBean(RuleDao.class);
            List<Rule> byRuleGroup = ruleDao.findByRuleGroup(ruleGroup);
            hiveDataSource = new ArrayList<>();
            for (Rule rule : byRuleGroup) {

                if (CollectionUtils.isNotEmpty(rule.getRuleDataSources())) {
                    for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
                        String tableName = ruleDataSource.getTableName();
                        if (StringUtils.isEmpty(tableName)) {
                            continue;
                        }
                        // UUID remove.
                        if (StringUtils.isNotBlank(ruleDataSource.getFileId()) && StringUtils.isNotBlank(tableName) && tableName.contains(SpecCharEnum.BOTTOM_BAR.getValue())
                                && tableName.length() - UuidGenerator.generate().length() - 1 > 0) {
                            tableName = tableName.substring(0, tableName.length() - UuidGenerator.generate().length() - 1);
                        }
                        // If type equals to data source
                        hiveDataSource.add(new HiveDataSourceDetail(ruleDataSource.getClusterName(), ruleDataSource.getDbName(), tableName));
                    }

                }

            }
            datasource.add(new DataSourceDetail(ruleGroup, hiveDataSource, scheduledFrontBackRule));

        }
        //hiveDataSource.clear();
    }

    public ScheduledTaskResponse(ScheduledFrontBackRule scheduledFrontBackRule) {
        ScheduledTask scheduledTask = scheduledFrontBackRule.getScheduledTask();
        this.scheduledTaskId = scheduledTask.getId();
        this.scheduleSystem = scheduledTask.getDispatchingSystemType();
        this.cluster = scheduledTask.getClusterName();
        this.projectName = scheduledTask.getProjectName();
        this.workFlow = scheduledTask.getWorkFlowName();
        this.taskName = scheduledTask.getTaskName();
        this.projectId = scheduledTask.getProject().getId();
        this.projectType = scheduledTask.getProject().getProjectType();
        this.releaseUser = scheduledTask.getReleaseUser();
        this.releaseStatus = scheduledTask.getReleaseStatus();
        datasource = new ArrayList<>();

        RuleGroup ruleGroup = scheduledFrontBackRule.getRuleGroup();
        RuleDao ruleDao = SpringContextHolder.getBean(RuleDao.class);
        List<Rule> byRuleGroup = ruleDao.findByRuleGroup(ruleGroup);
        hiveDataSource = new ArrayList<>();
        for (Rule rule : byRuleGroup) {

            if (CollectionUtils.isNotEmpty(rule.getRuleDataSources())) {
                for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
                    String tableName = ruleDataSource.getTableName();
                    if (StringUtils.isEmpty(tableName)) {
                        continue;
                    }
                    // UUID remove.
                    if (StringUtils.isNotBlank(ruleDataSource.getFileId()) && StringUtils.isNotBlank(tableName) && tableName.contains(SpecCharEnum.BOTTOM_BAR.getValue())
                            && tableName.length() - UuidGenerator.generate().length() - 1 > 0) {
                        tableName = tableName.substring(0, tableName.length() - UuidGenerator.generate().length() - 1);
                    }
                    // If type equals to data source
                    hiveDataSource.add(new HiveDataSourceDetail(ruleDataSource.getClusterName(), ruleDataSource.getDbName(), tableName));
                }

            }

        }
        datasource.add(new DataSourceDetail(ruleGroup, hiveDataSource, scheduledFrontBackRule));

        //hiveDataSource.clear();
    }


    public Long getScheduledTaskId() {
        return scheduledTaskId;
    }

    public void setScheduledTaskId(Long scheduledTaskId) {
        this.scheduledTaskId = scheduledTaskId;
    }

    public String getScheduleSystem() {
        return scheduleSystem;
    }

    public void setScheduleSystem(String scheduleSystem) {
        this.scheduleSystem = scheduleSystem;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getWorkFlow() {
        return workFlow;
    }

    public void setWorkFlow(String workFlow) {
        this.workFlow = workFlow;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<DataSourceDetail> getDatasource() {
        return datasource;
    }

    public void setDatasource(List<DataSourceDetail> datasource) {
        this.datasource = datasource;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getProjectType() {
        return projectType;
    }

    public void setProjectType(Integer projectType) {
        this.projectType = projectType;
    }

    public Integer getReleaseStatus() {
        return releaseStatus;
    }

    public void setReleaseStatus(Integer releaseStatus) {
        this.releaseStatus = releaseStatus;
    }
}
