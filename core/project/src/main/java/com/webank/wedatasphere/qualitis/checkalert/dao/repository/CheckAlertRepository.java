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

package com.webank.wedatasphere.qualitis.checkalert.dao.repository;

import com.webank.wedatasphere.qualitis.checkalert.entity.CheckAlert;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @author allenzhou
 */
public interface CheckAlertRepository extends JpaRepository<CheckAlert, Long> {

    /**
     * find by topic, workflow info
     * @param topic
     * @param workFlowName
     * @param workFlowVersion
     * @param workFlowSpace
     * @param projectId
     * @return
     */
    @Query(value = "select qac.* from qualitis_alert_config qac where qac.topic = ?1 and qac.work_flow_name = ?2 and qac.work_flow_version = ?3 and qac.work_flow_space = ?4 and qac.project_id = ?5", nativeQuery = true)
    CheckAlert findByTopicAndWorkflowInfo(String topic, String workFlowName, String workFlowVersion, String workFlowSpace, Long projectId);

    /**
     * find by rule group
     * @param ruleGroupInDb
     * @return
     */
    List<CheckAlert> findByRuleGroup(RuleGroup ruleGroupInDb);

    /**
     * find by project and workflow and topic
     * @param id
     * @param workflowName
     * @param topic
     * @return
     */
    @Query(value = "select qac.* from qualitis_alert_config qac where qac.project_id = ?1 and qac.work_flow_name = ?2 and qac.topic = ?3", nativeQuery = true)
    CheckAlert findByProjectAndWorkflowNameAndTopic(Long id, String workflowName, String topic);

    /**
     * find by project and topic with lowest version
     * @param projectId
     * @param topic
     * @return
     */
    @Query(value = "select qac.*, 0+RIGHT(work_flow_version, 6) AS workFlowVersion from qualitis_alert_config qac where qac.project_id = ?1 and qac.topic = ?2 ORDER BY workFlowVersion ASC limit 1", nativeQuery = true)
    CheckAlert findLowestVersionByProjectAndTopic(Long projectId, String topic);

    /**
     *count by project and topic
     * @param projectId
     * @param topic
     * @return
     */
    @Query(value = "select count(qac.id) from qualitis_alert_config qac where qac.project_id = ?1 and qac.topic = ?2", nativeQuery = true)
    int countByProjectAndTopic(Long projectId, String topic);

    /**
     * find by project and workflow and topics
     * @param projectId
     * @param workflowName
     * @param topics
     * @return
     */
    @Query(value = "select qac.* from qualitis_alert_config qac where qac.project_id = ?1 and qac.work_flow_name = ?2 and qac.topic in ?3", nativeQuery = true)
    List<CheckAlert> findByProjectAndWorkflowNameAndTopics(Long projectId, String workflowName, List<String> topics);

    /**
     * find by topic, workflow name, version and project
     * @param topic
     * @param workFlowName
     * @param version
     * @param projectId
     * @return
     */
    @Query(value = "select qac.id from qualitis_alert_config qac where qac.topic = ?1 and qac.work_flow_name = ?2 and qac.work_flow_version= ?3 and qac.project_id= ?4 ", nativeQuery = true)
    Long selectMateCheckAlert(String topic, String workFlowName, String version, Long projectId);


    /**
     * check Alert Query
     *
     * @param topic
     * @param alertTable
     * @param workFlowSpace
     * @param workFlowProject
     * @param workFlowName
     * @param nodeName
     * @param createUser
     * @param modifyUser
     * @param startCreateTime
     * @param endCreateTime
     * @param startModifyTime
     * @param endModifyTime
     * @param projectId
     * @param pageable
     * @return
     */
    @Query(value = "SELECT qr.* FROM qualitis_alert_config qr,qualitis_project qt where qr.project_id=qt.id and qr.project_id=?13 " +
            "and if(?1 is null, 1=1, qr.topic like ?1) and if(?2 is null, 1=1, qr.alert_table = ?2) and if(?3 is null,1=1, qr.work_flow_space = ?3) " +
            "and if(?4 is null,1=1, qt.name = ?4) and if(?5 is null,1=1,qr.work_flow_name = ?5) " +
            "and if(?6 is null,1=1, qr.node_name = ?6) " +
            "and if(?7 is null,1=1,qr.create_user = ?7) and if(?8 is null,1=1,qr.modify_user = ?8) " +
            "and if(?9 is null,1=1,qr.create_time >= ?9) and if(?10 is null,1=1,qr.create_time <= ?10) " +
            "and if(?11 is null,1=1,qr.modify_time >= ?11) and if(?12 is null,1=1,qr.modify_time <= ?12) " +
            "group by qr.id"
            , countQuery = "SELECT COUNT(0) FROM (SELECT qr.* FROM qualitis_alert_config qr,qualitis_project qt where qr.project_id=qt.id and qr.project_id=?13 " +
            "and if(?1 is null, 1=1, qr.topic like ?1) and if(?2 is null, 1=1, qr.alert_table = ?2) and if(?3 is null,1=1,qr.work_flow_space = ?3) " +
            "and if(?4 is null,1=1, qt.name = ?4) and if(?5 is null,1=1,qr.work_flow_name = ?5) " +
            "and if(?6 is null,1=1, qr.node_name = ?6) " +
            "and if(?7 is null,1=1,qr.create_user = ?7) and if(?8 is null,1=1,qr.modify_user = ?8) " +
            "and if(?9 is null,1=1,qr.create_time >= ?9) and if(?10 is null,1=1,qr.create_time <= ?10) " +
            "and if(?11 is null,1=1,qr.modify_time >= ?11) and if(?12 is null,1=1,qr.modify_time <= ?12) " +
            "group by qr.id) as a"
            , nativeQuery = true)
    Page<CheckAlert> checkAlertQuery(String topic, String alertTable, String workFlowSpace, String workFlowProject,
                                     String workFlowName, String nodeName, String createUser, String modifyUser, String startCreateTime,
                                     String endCreateTime, String startModifyTime, String endModifyTime, Long projectId, Pageable pageable);



    /**
     * get Deduplication Field
     *
     * @param projectId
     * @return
     */
    @Query(value ="select DISTINCT qa.work_flow_space as workFlowSpace,'' as workFlowProject,''as workFlowName,'' as nodeName,'' as alertTable from " +
            " qualitis_alert_config qa,qualitis_project qp where qa.project_id =qp.id and project_id =?1 and qa.work_flow_space is not null " +
            " union " +
            " select DISTINCT '' as workFlowSpace,qp.name as workFlowProject,''as workFlowName,'' as nodeName,'' as alertTable from " +
            " qualitis_alert_config qa,qualitis_project qp where qa.project_id =qp.id and project_id =?1 and qp.name is not null " +
            " union " +
            " select DISTINCT '' as workFlowSpace,'' as workFlowProject,qa.work_flow_name as workFlowName,'' as nodeName,'' as alertTable from " +
            " qualitis_alert_config qa,qualitis_project qp where qa.project_id =qp.id and project_id =?1 and qa.work_flow_name is not null " +
            " union " +
            " select DISTINCT '' as workFlowSpace,'' as workFlowProject,'' as workFlowName,qa.node_name as nodeName,'' as alertTable  from " +
            " qualitis_alert_config qa,qualitis_project qp where qa.project_id =qp.id and project_id =?1 and qa.node_name is not null " +
            " union " +
            " select DISTINCT '' as workFlowSpace,'' as workFlowProject,'' as workFlowName,'' as nodeName,qa.alert_table as alertTable  from " +
            " qualitis_alert_config qa,qualitis_project qp where qa.project_id =qp.id and project_id =?1 and qa.node_name is not null ",nativeQuery = true)
    List<Map<String,Object>> getDeduplicationField(Long projectId);
    }
