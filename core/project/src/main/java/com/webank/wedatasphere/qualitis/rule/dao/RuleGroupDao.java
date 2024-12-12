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

import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;

import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public interface RuleGroupDao {

    /**
     * Save RuleGroup
     *
     * @param ruleGroup
     * @return
     */
    RuleGroup saveRuleGroup(RuleGroup ruleGroup);

    /**
     * Find RuleGroup by id
     * @param id
     * @return
     */
    RuleGroup findById(Long id);

    /**
     * Delete RuleGroup
     * @param ruleGroup
     */
    void delete(RuleGroup ruleGroup);

    /**
     * Delete batch
     * @param ruleGroups
     */
    void deleteAll(List<RuleGroup> ruleGroups);

    /**
     * Find by rule group name and project id
     * @param ruleGroupName
     * @param projectId
     * @return
     */
    RuleGroup findByRuleGroupNameAndProjectId(String ruleGroupName, Long projectId);

    /**
     * Find rule group list by project id
     * @param projectId
     * @return
     */
    List<RuleGroup> findByProjectId(Long projectId);


    /**
     * Find By Project
     * @param projectId
     * @return
     */
    List<Map<String, Object>> findByProject(Long projectId);

    /**
     * Find By ProjectId And ExistRule
     * @param projectId
     * @return
     */
    List<RuleGroup> findByProjectIdAndExistRule(Long projectId);

    /**
     * Find By ProjectId And NotExistRule
     * @param projectId
     * @return
     */
    List<RuleGroup> findByProjectIdAndNotExistRule(Long projectId);

    /**
     * Find rules by ids
     * @param ruleGroupIds
     * @return
     */
    List<RuleGroup> findByIds(List<Long> ruleGroupIds);

    /**
     * Find latest version
     * @param ruleGroupName
     * @param projectId
     * @return
     */
    RuleGroup findLatestVersionByRuleGroupNameAndProjectId(String ruleGroupName, Long projectId);
}
