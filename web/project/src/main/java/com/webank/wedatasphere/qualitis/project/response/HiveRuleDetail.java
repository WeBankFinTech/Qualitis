/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.project.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.scheduled.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.ExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author howeye
 */
public class HiveRuleDetail {

    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("project_name")
    private String projectName;
    @JsonProperty("project_type")
    private Integer projectType;
    @JsonProperty("rule_id")
    private Long ruleId;
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("rule_cn_name")
    private String ruleCnName;
    @JsonProperty("rule_type")
    private Integer ruleType;
    private List<String> filter;
    @JsonProperty("rule_template_id")
    private Long ruleTemplateId;
    @JsonProperty("template_name")
    private String templateName;
    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("cluster_num")
    private Integer clusterNum;
    @JsonProperty("datasource")
    private List<HiveDataSourceDetail> hiveDataSource;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;
    @JsonProperty("rule_group_name")
    private String ruleGroupName;
    @JsonProperty("relation_object")
    private String relationObject;
    @JsonProperty("table_group")
    private Boolean tableGroup;

    @JsonProperty("work_flow_name")
    private String workFlowName;
    @JsonProperty("work_flow_version")
    private String workFlowVersion;
    @JsonProperty("work_flow_space")
    private String workFlowSpace;
    @JsonProperty("work_flow_project")
    private String workFlowProject;
    @JsonProperty("node_name")
    private String nodeName;
    @JsonProperty("rule_enable")
    private Boolean ruleEnable;
    @JsonProperty("union_way")
    private Integer unionWay;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("modify_user")
    private String modifyUser;
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("modify_time")
    private String modifyTime;


    public HiveRuleDetail() {
    }

    public HiveRuleDetail(Long ruleId, Long ruleGroupId, String ruleName, String ruleGroupName, String workFlowName, String workFlowVersion, Boolean enable, String workFlowSpace, String workFlowProject, String nodeName) {
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.ruleGroupId = ruleGroupId;
        this.ruleGroupName = ruleGroupName;
        this.workFlowName = workFlowName;
        this.workFlowVersion = workFlowVersion;
        this.workFlowSpace = workFlowSpace;
        this.workFlowProject = workFlowProject;
        this.nodeName = nodeName;
        this.ruleEnable = enable;
    }

    public HiveRuleDetail(Rule rule) {
        this.ruleId = rule.getId();
        this.ruleName = rule.getName();
        this.ruleCnName = rule.getCnName();
        this.ruleGroupId = rule.getRuleGroup().getId();
        this.ruleGroupName = rule.getRuleGroup().getRuleGroupName();
        this.projectId = rule.getProject().getId();
        this.projectType = rule.getProject().getProjectType();
        this.createUser = rule.getCreateUser();
        this.createTime = rule.getCreateTime();
        this.modifyUser = rule.getModifyUser();
        this.modifyTime = rule.getModifyTime();
        this.workFlowName = rule.getWorkFlowName();
        this.workFlowVersion = rule.getWorkFlowVersion();
        this.workFlowSpace = rule.getWorkFlowSpace();
        this.workFlowProject = rule.getProject().getName();
        this.nodeName = rule.getNodeName();

        if (StringUtils.isNotBlank(rule.getExecutionParametersName())) {
            ExecutionParameters executionParameters = SpringContextHolder.getBean(ExecutionParametersDao.class).findByNameAndProjectId(rule.getExecutionParametersName(), rule.getProject().getId());
            if (executionParameters != null) {
                this.ruleEnable = rule.getEnable();
                this.unionWay = executionParameters.getUnionWay();
            } else {
                this.unionWay = rule.getUnionWay();
                this.ruleEnable = rule.getEnable();
            }
        } else {
            this.unionWay = rule.getUnionWay();
            this.ruleEnable = rule.getEnable();
        }
        if (CollectionUtils.isNotEmpty(rule.getRuleGroup().getRuleDataSources())) {
            this.tableGroup = true;
        } else {
            this.tableGroup = false;
        }
        if (rule.getRuleType().equals(RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode())) {
            this.filter = rule.getRuleDataSources().stream().map(RuleDataSource::getFilter).collect(Collectors.toList());
        } else if (rule.getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
            this.filter = Collections.singletonList(rule.getWhereContent());
        } else if (rule.getRuleType().equals(RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode())) {
            rule.getRuleVariables()
                    .stream()
                    .filter(tp -> tp.getTemplateMidTableInputMeta() != null)
                    .filter(item -> TemplateInputTypeEnum.CONDITION.getCode().equals(item.getTemplateMidTableInputMeta().getInputType()))
                    .findAny()
                    .ifPresent(variable -> this.filter = Collections.singletonList(variable.getValue()));
        } else if (rule.getRuleType().equals(RuleTypeEnum.FILE_TEMPLATE_RULE.getCode())) {
            this.filter = rule.getRuleDataSources().stream().map(RuleDataSource::getFilter).collect(Collectors.toList());
        }
        this.ruleTemplateId = rule.getTemplate().getId();
        this.templateName = rule.getTemplate().getName();
        this.ruleType = rule.getRuleType();
        this.hiveDataSource = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(rule.getRuleDataSources())) {
            for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
                // If type equals to data source
                hiveDataSource.add(new HiveDataSourceDetail(ruleDataSource.getClusterName(), ruleDataSource.getDbName(), handleTableName(ruleDataSource)));
            }
        }

        Stream<String> clusterStream = hiveDataSource.stream().filter(item -> StringUtils.isNotEmpty(item.getCluster()))
                .map(hiveDataSourceDetail -> hiveDataSourceDetail.getCluster());

//            Don't to distinct if template is multi-cluster
        if (QualitisConstants.isAcrossCluster(rule.getTemplate().getEnName())) {
            this.clusterName = clusterStream
                    .collect(Collectors.joining(SpecCharEnum.COMMA.getValue()));
            this.clusterNum = 2;
        } else {
            this.clusterName = clusterStream
                    .distinct()
                    .collect(Collectors.joining(SpecCharEnum.COMMA.getValue()));
            this.clusterNum = 1;
        }

    }

    private String handleTableName(RuleDataSource ruleDataSource) {
        String tableName = ruleDataSource.getTableName();
        // UUID remove.
        if (StringUtils.isNotBlank(ruleDataSource.getFileId()) && StringUtils.isNotBlank(tableName) && tableName.contains(SpecCharEnum.BOTTOM_BAR.getValue())
                && tableName.length() - UuidGenerator.generate().length() - 1 > 0) {
            tableName = tableName.substring(0, tableName.length() - UuidGenerator.generate().length() - 1);
        }
        return tableName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Boolean getRuleEnable() {
        return ruleEnable;
    }

    public void setRuleEnable(Boolean ruleEnable) {
        this.ruleEnable = ruleEnable;
    }

    public Integer getClusterNum() {
        return clusterNum;
    }

    public void setClusterNum(Integer clusterNum) {
        this.clusterNum = clusterNum;
    }

    public Integer getProjectType() {
        return projectType;
    }

    public void setProjectType(Integer projectType) {
        this.projectType = projectType;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getRuleCnName() {
        return ruleCnName;
    }

    public void setRuleCnName(String ruleCnName) {
        this.ruleCnName = ruleCnName;
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

    public List<String> getFilter() {
        return filter;
    }

    public void setFilter(List<String> filter) {
        this.filter = filter;
    }

    public Long getRuleTemplateId() {
        return ruleTemplateId;
    }

    public void setRuleTemplateId(Long ruleTemplateId) {
        this.ruleTemplateId = ruleTemplateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public List<HiveDataSourceDetail> getHiveDataSource() {
        return hiveDataSource;
    }

    public void setHiveDataSource(List<HiveDataSourceDetail> hiveDataSource) {
        this.hiveDataSource = hiveDataSource;
    }

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public String getRelationObject() {
        return relationObject;
    }

    public void setRelationObject(String relationObject) {
        this.relationObject = relationObject;
    }

    public String getRuleGroupName() {
        return ruleGroupName;
    }

    public void setRuleGroupName(String ruleGroupName) {
        this.ruleGroupName = ruleGroupName;
    }

    public Boolean getTableGroup() {
        return tableGroup;
    }

    public void setTableGroup(Boolean tableGroup) {
        this.tableGroup = tableGroup;
    }

    public Integer getUnionWay() {
        return unionWay;
    }

    public void setUnionWay(Integer unionWay) {
        this.unionWay = unionWay;
    }

    public String getWorkFlowName() {
        return workFlowName;
    }

    public void setWorkFlowName(String workFlowName) {
        this.workFlowName = workFlowName;
    }

    public String getWorkFlowVersion() {
        return workFlowVersion;
    }

    public void setWorkFlowVersion(String workFlowVersion) {
        this.workFlowVersion = workFlowVersion;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getWorkFlowSpace() {
        return workFlowSpace;
    }

    public void setWorkFlowSpace(String workFlowSpace) {
        this.workFlowSpace = workFlowSpace;
    }

    public String getWorkFlowProject() {
        return workFlowProject;
    }

    public void setWorkFlowProject(String workFlowProject) {
        this.workFlowProject = workFlowProject;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public String toString() {
        return "HiveRuleDetail{" +
                "ruleId=" + ruleId +
                ", ruleName='" + ruleName + '\'' +
                ", ruleType=" + ruleType +
                ", filter=" + filter +
                ", ruleTemplateId=" + ruleTemplateId +
                ", templateName='" + templateName + '\'' +
                ", hiveDataSource=" + hiveDataSource +
                '}';
    }
}
