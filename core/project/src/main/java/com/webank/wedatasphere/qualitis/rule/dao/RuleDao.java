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

package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;

import java.util.List;

/**
 * @author howeye
 */
public interface RuleDao {

    /**
     * Find rule by project
     * @param project
     * @return
     */
    List<Rule> findByProject(Project project);

    /**
     * Save rule
     * @param rule
     * @return
     */
    Rule saveRule(Rule rule);

    /**
     * Find rule by id
     * @param ruleId
     * @return
     */
    Rule findById(Long ruleId);

    /**
     * Delete rule
     * @param rule
     */
    void deleteRule(Rule rule);

    /**
     * Find rules by ids
     * @param ruleIds
     * @return
     */
    List<Rule> findByIds(List<Long> ruleIds);

    /**
     * Find rule list by rule group
     * @param ruleGroup
     * @return
     */
    List<Rule> findByRuleGroup(RuleGroup ruleGroup);
}
