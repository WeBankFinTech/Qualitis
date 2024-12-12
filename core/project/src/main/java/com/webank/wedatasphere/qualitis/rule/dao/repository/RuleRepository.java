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

import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.entity.Template;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import javax.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 * @author howeye
 */
public interface RuleRepository extends JpaRepository<Rule, Long> {

    /**
     * Count rule by project
     * @param project
     * @return
     */
    @Query(value = "SELECT COUNT(qr.id) from Rule qr where qr.project = ?1")
    int countByProject(Project project);

    /**
     * Find by project with page.
     * @param project
     * @param pageable
     * @return
     */
    Page<Rule> findByProject(Project project, Pageable pageable);

    /**
     * Find rule by project
     * @param project
     * @return
     */
    List<Rule> findByProject(Project project);

    /**
     * Find all rule id, name by project
     * @param project
     * @return
     */
    @Query(value = "SELECT new map(qr.id as rule_id, qr.name as rule_name, qr.ruleGroup.id as rule_group_id, qr.ruleGroup.ruleGroupName as rule_group_name, qr.template as template,qr.workFlowName as work_flow_name,qr.workFlowVersion as work_flow_version,qr.enable as rule_enable,qr.workFlowSpace as work_flow_space,qr.nodeName as node_name ) FROM Rule qr where qr.project = ?1")
    List<Map<String, Object>> findSpecialInfoByProject(Project project);

    /**
     * Query rules
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
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qr.* FROM qualitis_rule qr, qualitis_rule_datasource rs,qualitis_rule_group ts,qualitis_project qp where qr.project_id=qp.id and qr.id = rs.rule_id and qr.rule_group_id = ts.id and qr.project_id = ?1 " +
            "and if(?2 is null, 1=1, qr.name like ?2) and if(?3 is null, 1=1, qr.cn_name like ?3) and (?4 is null or qr.template_id = ?4) " +
            "and (?5 is null or rs.db_name = ?5) and (?6 is null or rs.table_name = ?6) " +
            "and if(?7 is null,1=1, qr.enable = ?7) " +
            "and if(?8 is null,1=1,qr.create_user = ?8) and if(?9 is null,1=1,qr.modify_user = ?9) " +
            "and if(?10 is null,1=1,qr.create_time >= ?10) and if(?11 is null,1=1,qr.create_time <= ?11) " +
            "and if(?12 is null,1=1,qr.modify_time >= ?12) and if(?13 is null,1=1,qr.modify_time <= ?13) " +
            "and if(?14 is null,1=1,ts.rule_group_name like ?14) " +
            "and if(?15 is null,1=1,qr.work_flow_space = ?15) and if(?16 is null,1=1,qp.name = ?16) " +
            "and if(?17 is null,1=1,qr.work_flow_name = ?17) and if(?18 is null,1=1,qr.node_name = ?18) " +
            "group by qr.id"
            , countQuery =  "SELECT COUNT(0) FROM (SELECT qr.id FROM qualitis_rule qr, qualitis_rule_datasource rs,qualitis_rule_group ts,qualitis_project qp where qr.project_id=qp.id and qr.id = rs.rule_id and qr.rule_group_id = ts.id and qr.project_id = ?1 " +
            "and if(?2 is null, 1=1, qr.name like ?2) and if(?3 is null, 1=1, qr.cn_name like ?3)" +
            " and (?4 is null or qr.template_id = ?4) and (?5 is null or rs.db_name = ?5) and (?6 is null or rs.table_name = ?6) and (?7 is null or qr.enable = ?7) " +
            "and if(?8 is null,1=1,qr.create_user = ?8) and if(?9 is null,1=1,qr.modify_user = ?9) " +
            "and if(?10 is null,1=1,qr.create_time >= ?10) and if(?11 is null,1=1,qr.create_time <= ?11) " +
            "and if(?12 is null,1=1,qr.modify_time >= ?12) and if(?13 is null,1=1,qr.modify_time <= ?13) " +
            "and if(?14 is null,1=1,ts.rule_group_name like ?14) " +
            "and if(?15 is null,1=1,qr.work_flow_space = ?15) and if(?16 is null,1=1,qp.name = ?16) " +
            "and if(?17 is null,1=1,qr.work_flow_name = ?17) and if(?18 is null,1=1,qr.node_name = ?18) " +
            "group by qr.id) as a"
            ,nativeQuery = true)
    Page<Rule> findByConditionWithPage(Project project, String ruleName, String ruleCnName, Integer ruleTemplateId, String db, String table, Boolean ruleEnable
            , String createUser, String modifyUser, String startCreateTime, String endCreateTime, String startModifyTime, String endModifyTime
            , String ruleGroupName, String workFlowSpace, String workFlowProject, String workFlowName, String nodeName, Pageable pageable);

    /**
     * Find rule list by rule group
     * @param ruleGroup
     * @return
     */
    List<Rule> findByRuleGroup(RuleGroup ruleGroup);

    /**
     * Find rule by rule template
     * @param templateInDb
     * @return
     */
    List<Rule> findByTemplate(Template templateInDb);

    /**
     * Find rule by projectId，name
     * @param projectId
     * @param name
     * @return
     */
    @Query(value = "select q from Rule q where q.project.id =?1  AND q.executionParametersName=?2 ")
    List<Rule> getDeployExecutionParameters(Long projectId,String name);

    /**
     * Count by rule group
     * @param ruleGroup
     * @return
     */
    @Query(value = "SELECT COUNT(qr.id) from Rule qr where qr.ruleGroup = ?1")
    int countByRuleGroup(RuleGroup ruleGroup);

    /**
     * Find rule by group with conditions
     * @param ruleGroup
     * @param templateId
     * @param name
     * @param cnName
     * @param cols
     * @param ruleType
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qr FROM Rule qr WHERE (?5 IS NULL OR EXISTS (SELECT qrd.rule FROM RuleDataSource qrd WHERE qr = qrd.rule AND qrd.colName in ?5)) AND qr.ruleGroup = ?1 AND (?2 IS NULL OR template.id = ?2) AND (LENGTH(?3) = 0 OR name like ?3) AND (LENGTH(?4) = 0 OR cnName like ?4)" +
            " AND (?6 is null OR qr.ruleType = ?6)")
    Page<Rule> findByRuleGroupWithPage(RuleGroup ruleGroup, Long templateId, String name, String cnName, List<String> cols, Integer ruleType, Pageable pageable);

    /**
     * count By Rule Group With Page
     * @param ruleGroup
     * @param templateId
     * @param name
     * @param cnName
     * @param cols
     * @param ruleType
     * @return
     */
    @Query(value = "SELECT count(qr)  FROM Rule qr WHERE (?5 IS NULL OR EXISTS (SELECT qrd.rule FROM RuleDataSource qrd WHERE qr = qrd.rule AND qrd.colName in ?5)) AND qr.ruleGroup = ?1 AND (?2 IS NULL OR template.id = ?2) AND (LENGTH(?3) = 0 OR name like ?3) AND (LENGTH(?4) = 0 OR cnName like ?4)" +
            " AND (?6 is null OR qr.ruleType = ?6)")
    Long countByRuleGroupWithPage(RuleGroup ruleGroup, Long templateId, String name, String cnName, List<String> cols, Integer ruleType);

    /**
     * Find rule by group with conditions, join qualitis_rule_alarm_config
     * @param ruleGroup
     * @param templateId
     * @param name
     * @param cnName
     * @param cols
     * @param ruleType
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qr FROM Rule qr, AlarmConfig ac WHERE qr.id = ac.rule AND (?5 IS NULL OR EXISTS (SELECT qrd.rule FROM RuleDataSource qrd WHERE qr = qrd.rule AND qrd.colName in ?5)) AND qr.ruleGroup = ?1 " +
            " AND (?2 IS NULL OR ac.fileOutputName = ?2) AND (LENGTH(?3) = 0 OR qr.name like ?3) AND (LENGTH(?4) = 0 OR qr.cnName like ?4)" +
            " AND (?6 is null OR qr.ruleType = ?6)")
    Page<Rule> findByRuleGroupAndFileOutNameWithPage(RuleGroup ruleGroup, Long templateId, String name, String cnName, List<String> cols, Integer ruleType, Pageable pageable);

    /**
     * count By Rule Group With Page, join qualitis_rule_alarm_config
     * @param ruleGroup
     * @param templateId
     * @param name
     * @param cnName
     * @param cols
     * @param ruleType
     * @return
     */
    @Query(value = "SELECT count(qr) FROM Rule qr, AlarmConfig ac WHERE qr.id = ac.rule AND (?5 IS NULL OR EXISTS (SELECT qrd.rule FROM RuleDataSource qrd WHERE qr = qrd.rule AND qrd.colName in ?5)) AND qr.ruleGroup = ?1 " +
            " AND (?2 IS NULL OR ac.fileOutputName = ?2) AND (LENGTH(?3) = 0 OR qr.name like ?3) AND (LENGTH(?4) = 0 OR qr.cnName like ?4)" +
            " AND (?6 is null OR qr.ruleType = ?6)")
    Long countByRuleGroupAndFileOutNameWithPage(RuleGroup ruleGroup, Long templateId, String name, String cnName, List<String> cols, Integer ruleType);

    /**
     * Find by project and rule name
     * @param projectId
     * @param ruleName
     * @return
     */
    @Query(value = "select q from Rule q where q.project.id = ?1 AND q.name = ?2")
    Rule findByProjectAndRuleName(Long projectId, String ruleName);

    /**
     * Find Highest WorkFlowVersion
     * @param projectId
     * @param ruleName
     * @return
     */
    @Query(value = "SELECT q.*, 0+RIGHT(work_flow_version,6) AS workFlowVersion from qualitis_rule q where q.name =?2 and q.project_id =?1 ORDER BY workFlowVersion DESC limit 1 ",nativeQuery = true)
    Rule findHighestWorkFlowVersion(Long projectId, String ruleName);

    /**
     * Find Highest WorkFlowVersion
     * @param projectId
     * @param workflowName
     * @param ruleName
     * @return
     */
    @Query(value = "SELECT q.*, 0+RIGHT(work_flow_version,6) AS workFlowVersion from qualitis_rule q where q.name =?3 and q.work_flow_name = ?2 and q.project_id =?1 ORDER BY workFlowVersion DESC limit 1 ",nativeQuery = true)
    Rule findHighestVersionByProjectAndWorkFlowName(Long projectId, String workflowName, String ruleName);

    /**
     * Find Lowest WorkFlowVersion
     * @param projectId
     * @param ruleName
     * @return
     */
    @Query(value = "SELECT q.*, 0+RIGHT(work_flow_version,6) AS workFlowVersion from qualitis_rule q where q.name =?2 and q.project_id =?1 ORDER BY workFlowVersion ASC limit 1 ",nativeQuery = true)
    Rule findLowestWorkFlowVersion(Long projectId, String ruleName);

    /**
     * Paging rules
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param colName
     * @param user
     * @param ruleTemplateId
     * @param relationObjectType
     * @param pageable
     * @return
     */
    @Query(value = "select DISTINCT r.id from qualitis_rule_datasource ds " +
            "inner join qualitis_project_user pu on ds.project_id = pu.project_id " +
            "inner join qualitis_rule r on ds.rule_id = r.id " +
            "where (?1 is null or ds.cluster_name = ?1) and (?2 is null or ds.db_name = ?2) and (?3 is null or ds.table_name = ?3) and (?4 is null or ds.col_name = ?4)" +
            "and pu.user_name = ?5 and (?6 is null or r.template_id = ?6) " +
            "and if(?7 = 1, ds.col_name = '' or ds.col_name is null, if(?7 = 2, ds.col_name != '' and ds.col_name is not null, 1=1))"
            , countQuery = "select count(DISTINCT r.id) from qualitis_rule_datasource ds " +
                    "inner join qualitis_project_user pu on ds.project_id = pu.project_id " +
                    "inner join qualitis_rule r on ds.rule_id = r.id " +
                    "where (?1 is null or ds.cluster_name = ?1) and (?2 is null or ds.db_name = ?2) and (?3 is null or ds.table_name = ?3) and (?4 is null or ds.col_name = ?4) " +
                    "and pu.user_name = ?5 and (?6 is null or r.template_id = ?6) " +
                    " and if(?7 = 1, ds.col_name = '' or ds.col_name is null, if(?7 = 2, ds.col_name != '' and ds.col_name is not null, 1=1))"
            , nativeQuery = true)
    Page<BigInteger> findRuleByDataSource(String clusterName, String dbName, String tableName, String colName, String user, Long ruleTemplateId, Integer relationObjectType, Pageable pageable);

    /**
     * Count by ruleName and projectId
     * @param ruleName
     * @param projectId
     * @return
     */
    @Query(value = "select count(*) FROM qualitis_rule where name = ?1 and project_id= ?2 ", nativeQuery = true)
    int countByProjectAndRuleName(String ruleName,Long projectId);

    /**
     * select mate rule by ruleName workFlowName workFlowVersion
     * @param ruleName
     * @param workFlowName
     * @param workFlowVersion
     * @param projectId
     * @return
     */
    @Query(value = "SELECT qr.id FROM qualitis_rule qr where qr.name = ?1 and qr.work_flow_name = ?2 and qr.work_flow_version= ?3 and qr.project_id= ?4 ", nativeQuery = true)
    Long selectMateRule(String ruleName, String workFlowName, String workFlowVersion, Long projectId);

    /**
     * Find rule by project and rule name
     * @param id
     * @param workflowName
     * @param ruleNames
     * @return
     */
    @Query(value = "select q from Rule q where q.project.id = ?1 AND q.workFlowName = ?2 AND q.name in ?3")
    List<Rule> findByProjectAndWorkflowNameAndRuleNames(Long id, String workflowName, List<String> ruleNames);

    /**
     * find Exist Standard Vaule
     *
     * @param templateId
     * @param projectId
     * @return
     */
    @Query(value = "SELECT qr.* FROM qualitis_rule qr where qr.template_id = ?1 and qr.project_id=?2 and qr.standard_value_version_id is not null and qr.standard_value_version_en_name is not null ", nativeQuery = true)
    List<Rule> findExistStandardVaule(Long templateId, Long projectId);


    /**
     * find Custom Rule Type By Project
     *
     * @param ruleType
     * @param projectId
     * @return
     */
    @Query(value = "SELECT qr.* FROM qualitis_rule qr where qr.rule_type = ?1 and qr.project_id=?2 ", nativeQuery = true)
    List<Rule> findCustomRuleTypeByProject(Integer ruleType, Long projectId);

    /**
     * get Deduplication Field
     *
     * @param projectId
     * @return
     */
    @Query(value ="select DISTINCT qa.work_flow_space as workFlowSpace,'' as workFlowProject,''as workFlowName,'' as nodeName from " +
            " qualitis_rule qa,qualitis_project qp where qa.project_id =qp.id and project_id =?1 and qa.work_flow_space is not null " +
            " union " +
            " select DISTINCT '' as workFlowSpace,qp.name as workFlowProject,''as workFlowName,'' as nodeName from " +
            " qualitis_rule qa,qualitis_project qp where qa.project_id =qp.id and project_id =?1 and qp.name is not null " +
            " union " +
            " select DISTINCT '' as workFlowSpace,'' as workFlowProject,qa.work_flow_name as workFlowName,'' as nodeName from " +
            " qualitis_rule qa,qualitis_project qp where qa.project_id =qp.id and project_id =?1 and qa.work_flow_name is not null " +
            " union " +
            " select DISTINCT '' as workFlowSpace,'' as workFlowProject,'' as workFlowName,qa.node_name as nodeName from " +
            " qualitis_rule qa,qualitis_project qp where qa.project_id =qp.id and project_id =?1 and qa.node_name is not null ",nativeQuery = true)
    List<Map<String,Object>> findWorkFlowFiled(Long projectId);
}
