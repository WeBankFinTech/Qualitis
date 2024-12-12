package com.webank.wedatasphere.qualitis.response;

import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import java.util.List;

/**
 * @author allenzhou
 */
public class ProjectRuleResponse {
    private Project project;
    private List<Rule> rules;

    public ProjectRuleResponse() {
    }

    public ProjectRuleResponse(Project project) {
        this.project = project;
    }

    public ProjectRuleResponse(Project project, List<Rule> rules) {
        this.project = project;
        this.rules = rules;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }
}
