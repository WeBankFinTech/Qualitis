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

package com.webank.wedatasphere.qualitis.rule.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.rule.util.LazyGetUtil;
import org.hibernate.annotations.NotFoundAction;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.NotFound;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.UniqueConstraint;
import java.util.Objects;
import java.util.Set;

/**
 * @author howeye
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_rule", uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "name"}))
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cs_id")
    private String csId;

    @ManyToOne
    @JsonIgnore
    private Template template;

    @Column(length = 128)
    private String name;
    @Column(name = "cn_name", length = 128)
    private String cnName;

    @Column(length = 350)
    private String detail;

    @Column(name = "alert")
    private Boolean alert;

    @Column(name = "alert_level")
    private Integer alertLevel;

    @Column(name = "alert_receiver")
    private String alertReceiver;

    @OneToMany(mappedBy = "rule", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<RuleDataSource> ruleDataSources;

    @OneToMany(mappedBy = "rule", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<RuleDataSourceMapping> ruleDataSourceMappings;

    @Column(name = "alarm")
    private Boolean alarm;

    @Column(name = "rule_type")
    private Integer ruleType;

    @OneToMany(mappedBy = "rule", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<AlarmConfig> alarmConfigs;

    @OneToMany(mappedBy = "rule", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<RuleVariable> ruleVariables;

    private transient volatile int ruleVariableSize;

    @ManyToOne
    @JsonIgnore
    private Project project;

    @Column(name = "rule_template_name")
    private String ruleTemplateName;

    @Column(name = "function_type")
    private Integer functionType;
    @Column(name = "function_content", length = 3010)
    private String functionContent;
    @Column(name = "from_content", length = 3010)
    private String fromContent;
    @Column(name = "where_content", length = 3010)
    private String whereContent;
    @Column(name = "output_name", length = 170)
    private String outputName;

    @Column(name = "abort_on_failure")
    private Boolean abortOnFailure;

    @Column(name = "create_user", length = 50)
    private String createUser;
    @Column(name = "create_time", length = 25)
    private String createTime;
    @Column(name = "modify_user", length = 50)
    private String modifyUser;
    @Column(name = "modify_time", length = 25)
    private String modifyTime;
    @Column(name = "delete_fail_check_result")
    private Boolean deleteFailCheckResult;

    @Column(name = "specify_static_startup_param")
    private Boolean specifyStaticStartupParam;
    @Column(name = "static_startup_param")
    private String staticStartupParam;

    @ManyToOne
    @JsonIgnore
    private RuleGroup ruleGroup;

    @Column(name = "bash_content", length = 1000)
    private String bashContent;

    @Column(name = "execution_parameters_name")
    private String executionParametersName;

    @Column(name = "abnormal_database",length = 100)
    private String abnormalDatabase;

    @Column(name = "cluster",length = 100)
    private String abnormalCluster;

    @Column(name = "abnormal_proxy_user", length = 50)
    private String abnormalProxyUser;

    @Column(name = "work_flow_name")
    private String workFlowName;

    @Column(name = "work_flow_version")
    private String workFlowVersion;

    @Column(name = "enable")
    private Boolean enable = true;

    @Column(name = "rule_no")
    private Integer ruleNo;

    @JsonProperty("union_all")
    @Column(name = "union_all")
    private Boolean unionAll;

    @Column(name = "contrast_type")
    private Integer contrastType;

    @Column(name = "work_flow_space")
    private String workFlowSpace;

    @Column(name = "node_name")
    private String nodeName;

    public Rule() {
        // Default Constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCsId() {
        return csId;
    }

    public void setCsId(String csId) {
        this.csId = csId;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Boolean getAlert() {
        return alert;
    }

    public void setAlert(Boolean alert) {
        this.alert = alert;
    }

    public Integer getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(Integer alertLevel) {
        this.alertLevel = alertLevel;
    }

    public String getAlertReceiver() {
        return alertReceiver;
    }

    public void setAlertReceiver(String alertReceiver) {
        this.alertReceiver = alertReceiver;
    }

    public Set<RuleDataSource> getRuleDataSources() {
        return ruleDataSources;
    }

    public void setRuleDataSources(Set<RuleDataSource> ruleDataSources) {
        this.ruleDataSources = ruleDataSources;
    }

    public Set<RuleDataSourceMapping> getRuleDataSourceMappings() {
        return ruleDataSourceMappings;
    }

    public void setRuleDataSourceMappings(Set<RuleDataSourceMapping> ruleDataSourceMappings) {
        this.ruleDataSourceMappings = ruleDataSourceMappings;
    }

    public Boolean getAlarm() {
        return alarm;
    }

    public void setAlarm(Boolean alarm) {
        this.alarm = alarm;
    }

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    public Set<AlarmConfig> getAlarmConfigs() {
        return alarmConfigs;
    }

    public void setAlarmConfigs(Set<AlarmConfig> alarmConfigs) {
        this.alarmConfigs = alarmConfigs;
    }

    public Set<RuleVariable> getRuleVariables() {
        if (ruleVariableSize == 0) {
            this.ruleVariables = LazyGetUtil.getRuleVariables(this);
            this.ruleVariableSize = this.ruleVariables.size();
        }
        return this.ruleVariables;
    }

    public void setRuleVariables(Set<RuleVariable> ruleVariables) {
        this.ruleVariables = ruleVariables;
        this.ruleVariableSize = this.ruleVariables != null ? this.ruleVariables.size() : 0;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getRuleTemplateName() {
        return ruleTemplateName;
    }

    public void setRuleTemplateName(String ruleTemplateName) {
        this.ruleTemplateName = ruleTemplateName;
    }

    public Integer getFunctionType() {
        return functionType;
    }

    public void setFunctionType(Integer functionType) {
        this.functionType = functionType;
    }

    public String getFunctionContent() {
        return functionContent;
    }

    public void setFunctionContent(String functionContent) {
        this.functionContent = functionContent;
    }

    public String getFromContent() {
        return fromContent;
    }

    public void setFromContent(String fromContent) {
        this.fromContent = fromContent;
    }

    public String getWhereContent() {
        return whereContent;
    }

    public void setWhereContent(String whereContent) {
        this.whereContent = whereContent;
    }

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    public Boolean getAbortOnFailure() {
        return abortOnFailure;
    }

    public void setAbortOnFailure(Boolean abortOnFailure) {
        this.abortOnFailure = abortOnFailure;
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

    public Boolean getDeleteFailCheckResult() {
        return deleteFailCheckResult;
    }

    public void setDeleteFailCheckResult(Boolean deleteFailCheckResult) {
        this.deleteFailCheckResult = deleteFailCheckResult;
    }

    public Boolean getSpecifyStaticStartupParam() {
        return specifyStaticStartupParam;
    }

    public void setSpecifyStaticStartupParam(Boolean specifyStaticStartupParam) {
        this.specifyStaticStartupParam = specifyStaticStartupParam;
    }

    public String getStaticStartupParam() {
        return staticStartupParam;
    }

    public void setStaticStartupParam(String staticStartupParam) {
        this.staticStartupParam = staticStartupParam;
    }

    public RuleGroup getRuleGroup() {
        return ruleGroup;
    }

    public void setRuleGroup(RuleGroup ruleGroup) {
        this.ruleGroup = ruleGroup;
    }

    public String getBashContent() {
        return bashContent;
    }

    public void setBashContent(String bashContent) {
        this.bashContent = bashContent;
    }

    public String getExecutionParametersName() {
        return executionParametersName;
    }

    public void setExecutionParametersName(String executionParametersName) {
        this.executionParametersName = executionParametersName;
    }

    public String getAbnormalDatabase() {
        return abnormalDatabase;
    }

    public void setAbnormalDatabase(String abnormalDatabase) {
        this.abnormalDatabase = abnormalDatabase;
    }

    public String getAbnormalCluster() {
        return abnormalCluster;
    }

    public void setAbnormalCluster(String abnormalCluster) {
        this.abnormalCluster = abnormalCluster;
    }

    public String getAbnormalProxyUser() {
        return abnormalProxyUser;
    }

    public void setAbnormalProxyUser(String abnormalProxyUser) {
        this.abnormalProxyUser = abnormalProxyUser;
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

    public Integer getRuleNo() {
        return ruleNo;
    }

    public void setRuleNo(Integer ruleNo) {
        this.ruleNo = ruleNo;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean getUnionAll() {
        return unionAll;
    }

    public void setUnionAll(Boolean unionAll) {
        this.unionAll = unionAll;
    }

    public Integer getContrastType() {
        return contrastType;
    }

    public void setContrastType(Integer contrastType) {
        this.contrastType = contrastType;
    }

    public int getRuleVariableSize() {
        return ruleVariableSize;
    }

    public void setRuleVariableSize(int ruleVariableSize) {
        this.ruleVariableSize = ruleVariableSize;
    }

    public String getWorkFlowSpace() {
        return workFlowSpace;
    }

    public void setWorkFlowSpace(String workFlowSpace) {
        this.workFlowSpace = workFlowSpace;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Rule rule = (Rule) o;
        return Objects.equals(id, rule.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Rule{" +
            "id=" + id +
            ", csId='" + csId + '\'' +
            ", template=" + template +
            ", name='" + name + '\'' +
            ", cnName='" + cnName + '\'' +
            ", detail='" + detail + '\'' +
            ", alert=" + alert +
            ", alertLevel=" + alertLevel +
            ", alertReceiver='" + alertReceiver + '\'' +
            ", alarm=" + alarm +
            ", ruleType=" + ruleType +
            ", project=" + project +
            ", ruleTemplateName='" + ruleTemplateName + '\'' +
            ", functionType=" + functionType +
            ", functionContent='" + functionContent + '\'' +
            ", fromContent='" + fromContent + '\'' +
            ", whereContent='" + whereContent + '\'' +
            ", outputName='" + outputName + '\'' +
            ", abortOnFailure=" + abortOnFailure +
            ", createUser='" + createUser + '\'' +
            ", createTime='" + createTime + '\'' +
            ", modifyUser='" + modifyUser + '\'' +
            ", modifyTime='" + modifyTime + '\'' +
            ", deleteFailCheckResult=" + deleteFailCheckResult +
            ", specifyStaticStartupParam=" + specifyStaticStartupParam +
            ", staticStartupParam='" + staticStartupParam + '\'' +
            ", ruleGroup=" + ruleGroup +
            ", bashContent='" + bashContent + '\'' +
            ", executionParametersName='" + executionParametersName + '\'' +
            ", abnormalDatabase='" + abnormalDatabase + '\'' +
            ", abnormalCluster='" + abnormalCluster + '\'' +
            ", abnormalProxyUser='" + abnormalProxyUser + '\'' +
            ", workFlowName='" + workFlowName + '\'' +
            ", workFlowVersion='" + workFlowVersion + '\'' +
            ", ruleNo=" + ruleNo +
            ", enable=" + enable +
            ", unionAll=" + unionAll +
            ", contrastType=" + contrastType +
            '}';
    }
}
