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

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import java.util.Set;
import java.util.stream.Stream;
import javax.persistence.*;
import org.hibernate.annotations.NotFound;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.NotFoundAction;

/**
 * @author howeye
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_rule_group")
public class RuleGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_group_name")
    private String ruleGroupName;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "version")
    private String version;

    @OneToMany(mappedBy = "ruleGroup", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<RuleDataSource> ruleDataSources;

    @Column(name = "node_name")
    private String nodeName;

    @Column(name = "type")
    private Integer type;

    public RuleGroup() {
    }

    public RuleGroup(String ruleGroupName, Long projectId) {
        this.ruleGroupName = ruleGroupName;
        this.projectId = projectId;
    }

    public RuleGroup(String ruleGroupName, Long projectId, String version) {
        this.ruleGroupName = ruleGroupName;
        this.projectId = projectId;
        this.version = version;
    }

    public RuleGroup(String ruleGroupName, Long projectId, int groupType) {
        this.ruleGroupName = ruleGroupName;
        this.projectId = projectId;
        this.type = groupType;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuleGroupName() {
        return ruleGroupName;
    }

    public void setRuleGroupName(String ruleGroupName) {
        this.ruleGroupName = ruleGroupName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Set<RuleDataSource> getRuleDataSources() {
        return ruleDataSources;
    }

    public void setRuleDataSources(Set<RuleDataSource> ruleDataSources) {
        this.ruleDataSources = ruleDataSources;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "RuleGroup{" +
            "id=" + id +
            ", ruleGroupName='" + ruleGroupName + '\'' +
            '}';
    }

}
