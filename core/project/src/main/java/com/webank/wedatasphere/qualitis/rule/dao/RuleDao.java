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
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public interface RuleDao {

    /**
     * Count by project
     *
     * @param project
     * @return
     */
    int countByProject(Project project);

    /**
     * Find rule by project
     *
     * @param project
     * @return
     */
    List<Rule> findByProject(Project project);

    /**
     * Find by project with page.
     *
     * @param project
     * @param page
     * @param size
     * @return
     */
    List<Rule> findByProjectWithPage(Project project, int page, int size);

    /**
     * Find all rule id, name by project
     *
     * @param project
     * @param ruleName
     * @return
     */
    List<Map<String, Object>> findSpecialInfoByProject(Project project, String ruleName);

    /**
     * find rules <id, name> by project and some conditions
     *
     * @param project
     * @param ruleName
     * @param ruleCnName
     * @param ruleTemplateId
     * @param db
     * @param table
     * @param ruleEnable
     * @param createUser
     * @param modifyUser
     * @param startCreateTime
     * @param endCreateTime
     * @param startModifyTime
     * @param endModifyTime
     * @param ruleGroupName
     * @param workFlowSpace
     * @param workFlowProject
     * @param workFlowName
     * @param nodeName
     * @param page
     * @param size
     * @return
     */
    Page<Rule> findByConditionWithPage(Project project, String ruleName, String ruleCnName, Integer ruleTemplateId, String db, String table, Boolean ruleEnable
            , String createUser, String modifyUser, String startCreateTime, String endCreateTime, String startModifyTime, String endModifyTime, String ruleGroupName,
                                       String workFlowSpace, String workFlowProject, String workFlowName, String nodeName, int page, int size);

    /**
     * Find rule by project and rule name
     *
     * @param project
     * @param ruleName
     * @return
     */
    Rule findByProjectAndRuleName(Project project, String ruleName);

    /**
     * Find rule by workflow name and project
     *
     * @param project
     * @param workflowName
     * @param ruleName
     * @return
     */
    Rule findHighestVersionByProjectAndWorkFlowName(Project project, String workflowName, String ruleName);

    /**
     * Find rule by project and rule names
     *
     * @param project
     * @param workflowName
     * @param ruleNames
     * @return
     */
    List<Rule> findByProjectAndWorkflowNameAndRuleNames(Project project, String workflowName, List<String> ruleNames);

    /**
     * Save rule
     *
     * @param rule
     * @return
     */
    Rule saveRule(Rule rule);

    /**
     * Find rule by id
     *
     * @param ruleId
     * @return
     */
    Rule findById(Long ruleId);

    /**
     * Delete rule
     *
     * @param rule
     */
    void deleteRule(Rule rule);

    /**
     * Delete by id
     *
     * @param ruleId
     */
    void deleteById(Long ruleId);

    /**
     * Find rules by ids
     *
     * @param ruleIds
     * @return
     */
    List<Rule> findByIds(List<Long> ruleIds);

    /**
     * Count rule list by rule group
     *
     * @param ruleGroup
     * @return
     */
    int countByRuleGroup(RuleGroup ruleGroup);

    /**
     * Find rule list by rule group
     *
     * @param ruleGroup
     * @return
     */
    List<Rule> findByRuleGroup(RuleGroup ruleGroup);

    /**
     * Find rule list by rule group with page
     *
     * @param page
     * @param size
     * @param ruleGroup
     * @param templateId
     * @param name
     * @param cnName
     * @param cols
     * @param ruleType
     * @return
     */
    Page<Rule> findByRuleGroupWithPage(int page, int size, RuleGroup ruleGroup, Long templateId, String name, String cnName, List<String> cols, Integer ruleType);

    /**
     * find By Rule Group And File Out Name With Page
     *
     * @param page
     * @param size
     * @param ruleGroup
     * @param templateId
     * @param name
     * @param cnName
     * @param cols
     * @param ruleType
     * @return
     */
    List<Rule> findByRuleGroupAndFileOutNameWithPage(int page, int size, RuleGroup ruleGroup, Long templateId, String name, String cnName, List<String> cols, Integer ruleType);

    /**
     * count By Rule Group And File Out Name
     *
     * @param ruleGroup
     * @param templateId
     * @param name
     * @param cnName
     * @param cols
     * @param ruleType
     * @return
     */
    Long countByRuleGroupAndFileOutName(RuleGroup ruleGroup, Long templateId, String name, String cnName, List<String> cols, Integer ruleType);

    /**
     * Find rule by rule template
     *
     * @param templateInDb
     * @return
     */
    List<Rule> findByTemplate(Template templateInDb);

    /**
     * Find rule by idList
     *
     * @param idList
     * @return
     */
    List<Rule> getDeployStandardVersionIdList(List<Long> idList);

    /**
     * Find rule by StandardVersionId
     *
     * @param standardVersionId
     * @return
     */
    List<Rule> getDeployStandardVersionId(long standardVersionId);

    /**
     * Find rule by projectId，name
     *
     * @param projectId
     * @param name
     * @return
     */
    Long countDeployExecutionParameters(Long projectId, String name);

    /**
     * Paging Rule by datasource
     *
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param colName
     * @param user
     * @param ruleTemplateId
     * @param relationObjectType
     * @param page
     * @param size
     * @return
     */
    Page<Rule> findRuleByDataSource(String clusterName, String dbName, String tableName, String colName, String user, Long ruleTemplateId, Integer relationObjectType, int page, int size);

    /**
     * Count by ruleName and projectId
     *
     * @param ruleName
     * @param projectId
     * @return
     */
    int countByProjectAndRuleName(String ruleName, Long projectId);

    /**
     * select mate rule by ruleName  workFlowName workFlowVersion
     *
     * @param ruleName
     * @param workFlowName
     * @param workFlowVersion
     * @param projectId
     * @return
     */
    Long selectMateRule(String ruleName, String workFlowName, String workFlowVersion, Long projectId);

    /**
     * find MinWorkFlowVersion
     *
     * @param ruleName
     * @param projectId
     * @return
     */
    Rule findMinWorkFlowVersionRule(String ruleName, Long projectId);

    /**
     * find All By Id
     *
     * @param ruleIds
     * @return
     */
    List<Rule> findAllById(List<Long> ruleIds);

    /**
     * save Rules
     *
     * @param rules
     * @return
     */
    List<Rule> saveRules(List<Rule> rules);

    /**
     * find Exist Standard Vaule
     *
     * @param projectId
     * @return
     */
    List<Rule> findExistStandardVaule(Long projectId);

    /**
     * find Custom Rule Type By Project
     *
     * @param ruleType
     * @param projectId
     * @return
     */
    List<Rule> findCustomRuleTypeByProject(Integer ruleType, Long projectId);

    /**
     * find Work Flow Filed
     *
     * @param projectId
     * @return
     */
    List<Map<String,Object>> findWorkFlowFiled(Long projectId);

    /**
     * find ByIds And Project
     *
     * @param ruleIds
     * @param projectId
     * @return
     */
    List<Rule> findByIdsAndProject(List<Long> ruleIds, Long projectId);
}
