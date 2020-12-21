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

package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RuleRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author howeye
 */
@Repository
public class RuleDaoImpl implements RuleDao {

    @Autowired
    private RuleRepository ruleRepository;

    @Override
    public List<Rule> findByProject(Project project) {
        return ruleRepository.findByProject(project);
    }

    @Override
    public Rule findByProjectAndRuleName(Project project, String ruleName) {
        List<Rule> rules = findByProject(project);
        for (Rule rule: rules) {
            if (rule.getName().equals(ruleName)) {
                return rule;
            }
        }
        return null;
    }

    @Override
    public Rule saveRule(Rule rule) {
        return ruleRepository.save(rule);
    }

    @Override
    public Rule findById(Long ruleId) {
        return ruleRepository.findById(ruleId).orElse(null);
    }

    @Override
    public void deleteRule(Rule rule) {
        ruleRepository.delete(rule);
    }

    @Override
    public List<Rule> findByIds(List<Long> ruleIds) {
        return ruleRepository.findAllById(ruleIds);
    }

    @Override
    public List<Rule> findByRuleGroup(RuleGroup ruleGroup) {
        return ruleRepository.findByRuleGroup(ruleGroup);
    }

    @Override
    public List<Rule> findByTemplate(Template templateInDb) {
        return ruleRepository.findByTemplate(templateInDb);
    }
}
