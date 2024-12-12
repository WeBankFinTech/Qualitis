package com.webank.wedatasphere.qualitis.checkalert.dao;

import com.webank.wedatasphere.qualitis.checkalert.entity.CheckAlert;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author allenzhou@webank.com
 * @date 2023/3/1 15:40
 */
public interface CheckAlertDao {
    /**
     * Save
     * @param checkAlert
     * @return
     */
    CheckAlert save(CheckAlert checkAlert);

    /**
     * find by id
     * @param checkAlertId
     * @return
     */
    CheckAlert findById(Long checkAlertId);

    /**
     * delete
     * @param checkAlert
     */
    void delete(CheckAlert checkAlert);

    /**
     * delete all
     * @param checkAlerts
     */
    void deleteAll(List<CheckAlert> checkAlerts);

    /**
     * find by topic, workflow info
     * @param topic
     * @param workFlowName
     * @param workFlowVersion
     * @param workFlowSpace
     * @param projectId
     * @return
     */
    CheckAlert findByTopicAndWorkflowInfo(String topic, String workFlowName, String workFlowVersion, String workFlowSpace, Long projectId);

    /**
     * find by rule group
     * @param ruleGroupInDb
     * @return
     */
    List<CheckAlert> findByRuleGroup(RuleGroup ruleGroupInDb);

    /**
     * find by project and workflow and topic
     * @param project
     * @param workflowName
     * @param topic
     * @return
     */
    CheckAlert findByProjectAndWorkflowNameAndTopic(Project project, String workflowName, String topic);

    /**
     * find by project and topic with lowest version
     * @param projectId
     * @param topic
     * @return
     */
    CheckAlert findLowestVersionByProjectAndTopic(Long projectId, String topic);

    /**
     * count by project and topic
     * @param projectId
     * @param topic
     * @return
     */
    int countByProjectAndTopic(Long projectId, String topic);

    /**
     * find by project and workflow and topics
     * @param project
     * @param workflowName
     * @param topics
     * @return
     */
    List<CheckAlert> findByProjectAndWorkflowNameAndTopics(Project project, String workflowName, List<String> topics);

    /**
     * find by topic, workflow name, version and project
     * @param topic
     * @param workFlowName
     * @param version
     * @param projectId
     * @return
     */
    Long selectMateCheckAlert(String topic, String workFlowName, String version, Long projectId);

    /**
     * get Deduplication Field
     * @return
     * @param projectId
     */
    List<Map<String,Object>> getDeduplicationField(Long projectId);

    /**
     * check Alert Query
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
     * @param page
     * @param size
     * @return
     */
    Page<CheckAlert> checkAlertQuery(String topic, String alertTable, String workFlowSpace, String workFlowProject, String workFlowName, String nodeName
            , String createUser, String modifyUser, String startCreateTime, String endCreateTime, String startModifyTime, String endModifyTime, Long projectId, int page, int size);

    /**
     * Save all
     * @param checkAlerts
     * @return
     */
    List<CheckAlert> saveAll(List<CheckAlert> checkAlerts);
}
