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

package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author howeye
 */
public interface RuleGroupRepository extends JpaRepository<RuleGroup, Long> {

    /**
     * Find rule group by rule group name and project id
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
     * find By Project
     * @param projectId
     * @return
     */
    @Query(value = "select new map(id as rule_group_id, ruleGroupName as rule_group_name, version as version) " +
            "from RuleGroup where id in (select r.ruleGroup from Rule r where r.project.id=?1) group by rule_group_id, ruleGroupName")
    List<Map<String, Object>> findByProject(Long projectId);

    /**
     * find By ProjectId And ExistRule
     * @param projectId
     * @return
     */
    @Query(value = "select q.* from qualitis_rule_group q where q.project_id = ?1 and exists (select qr.* from qualitis_rule qr where qr.rule_group_id = q.id)", nativeQuery = true)
    List<RuleGroup> findByProjectIdAndExistRule(Long projectId);

    /**
     * find By ProjectId And ExistRule
     * @param projectId
     * @return
     */
    @Query(value = "select q.* from qualitis_rule_group q where q.project_id = ?1 and not exists (select qr.* from qualitis_rule qr where qr.rule_group_id = q.id) and not exists (select qac.* from qualitis_alert_config qac where qac.rule_group_id = q.id)", nativeQuery = true)
    List<RuleGroup> findByProjectIdAndNotExistRule(Long projectId);

    /**
     * Find latest version
     * @param ruleGroupName
     * @param projectId
     * @return
     */
    @Query(value = "select q.* from qualitis_rule_group q where q.rule_group_name = ?1 and q.project_id = ?2 and q.version is null", nativeQuery = true)
    RuleGroup findLatestVersionByRuleGroupNameAndProjectId(String ruleGroupName, Long projectId);
}
