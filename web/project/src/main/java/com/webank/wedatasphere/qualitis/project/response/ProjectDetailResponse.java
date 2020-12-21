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
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectLabel;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
public class ProjectDetailResponse {

    @JsonProperty("project_detail")
    private ProjectDetail projectDetail;
    @JsonProperty("rule_details")
    private List<HiveRuleDetail> ruleDetails;

    public ProjectDetailResponse() {
    }

    public ProjectDetailResponse(Project project, List<Rule> ruleList) {
        projectDetail = new ProjectDetail();
        BeanUtils.copyProperties(project, projectDetail);
        projectDetail.setProjectId(project.getId());
        projectDetail.setProjectName(project.getName());
        projectDetail.setCreateUser(project.getCreateUser());
        Set<ProjectLabel> labelSet = project.getProjectLabels();
        if (labelSet != null && ! labelSet.isEmpty()) {
            Set<String> labels = new HashSet<>();
            for (ProjectLabel projectLabel : labelSet) {
                labels.add(projectLabel.getLabelName());
            }
            projectDetail.setProjectLabels(labels);
        }
        ruleDetails = new ArrayList<>();
        if (null != ruleList) {
            for (Rule rule : ruleList) {
                ruleDetails.add(new HiveRuleDetail(rule));
            }
        }
    }

    public ProjectDetail getProjectDetail() {
        return projectDetail;
    }

    public void setProjectDetail(ProjectDetail projectDetail) {
        this.projectDetail = projectDetail;
    }

    public List<HiveRuleDetail> getRuleDetails() {
        return ruleDetails;
    }

    public void setRuleDetails(List<HiveRuleDetail> ruleDetails) {
        this.ruleDetails = ruleDetails;
    }

    @Override
    public String toString() {
        return "ProjectDetailResponse{" +
                "projectDetail=" + projectDetail +
                ", ruleDetails=" + ruleDetails +
                '}';
    }
}
