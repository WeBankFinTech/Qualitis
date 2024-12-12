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

package com.webank.wedatasphere.qualitis.entity;

import javax.persistence.*;

/**
 * @author howeye
 */
@Entity
@Table(name = "qualitis_application_task_result")
public class TaskResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_id")
    private String applicationId;
    @Column(name = "rule_id")
    private Long ruleId;
    private String value;
    @Column(name = "result_type")
    private String resultType;
    @Column(name = "create_time")
    private String createTime;
    @Column(name = "save_result")
    private Boolean saveResult;
    @Column(name = "rule_metric_id")
    private Long ruleMetricId;
    @Column(name = "run_date")
    private Long runDate;
    @Column(name = "department_code")
    private String departmentCode;

    public TaskResult() {
        // Default Constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Boolean getSaveResult() {
        return saveResult;
    }

    public void setSaveResult(Boolean saveResult) {
        this.saveResult = saveResult;
    }

    public Long getRuleMetricId() {
        return ruleMetricId;
    }

    public void setRuleMetricId(Long ruleMetricId) {
        this.ruleMetricId = ruleMetricId;
    }

    public Long getRunDate() {
        return runDate;
    }

    public void setRunDate(Long runDate) {
        this.runDate = runDate;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }
}
