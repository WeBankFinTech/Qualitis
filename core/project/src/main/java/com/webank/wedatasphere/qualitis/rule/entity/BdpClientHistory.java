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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author howeye
 */
@Entity
@Table(name = "qualitis_bdp_client_history", uniqueConstraints = @UniqueConstraint(columnNames = {"rule_id"}))
public class BdpClientHistory {
    @Id
    @Column(name = "rule_id")
    private Long ruleId;

    @Column(name = "template_function")
    private String templateFunction;

    @Column(name = "datasource")
    private String datasource;

    @Column(name = "project_name")
    private String projectName;

    public BdpClientHistory() {
        // Default Constructor
    }

    public BdpClientHistory(Long ruleId, String templateFunction) {
        this.ruleId = ruleId;
        this.templateFunction = templateFunction;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getTemplateFunction() {
        return templateFunction;
    }

    public void setTemplateFunction(String templateFunction) {
        this.templateFunction = templateFunction;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
