package com.webank.wedatasphere.qualitis.checkalert.dao.impl;

import com.webank.wedatasphere.qualitis.checkalert.dao.CheckAlertDao;
import com.webank.wedatasphere.qualitis.checkalert.dao.repository.CheckAlertRepository;
import com.webank.wedatasphere.qualitis.checkalert.entity.CheckAlert;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author allenzhou@webank.com
 * @date 2023/3/1 15:43
 */
@Repository
public class CheckAlertDaoImpl implements CheckAlertDao {

    @Autowired
    private CheckAlertRepository checkAlertRepository;

    @Override
    public CheckAlert save(CheckAlert checkAlert) {
        return checkAlertRepository.save(checkAlert);
    }

    @Override
    public CheckAlert findById(Long checkAlertId) {
        if (checkAlertRepository.findById(checkAlertId).isPresent()) {
            return checkAlertRepository.findById(checkAlertId).get();
        } else {
            return null;
        }
    }

    @Override
    public void delete(CheckAlert checkAlert) {
        checkAlertRepository.delete(checkAlert);
    }

    @Override
    public void deleteAll(List<CheckAlert> checkAlerts) {
        checkAlertRepository.deleteAll(checkAlerts);
    }

    @Override
    public CheckAlert findByTopicAndWorkflowInfo(String topic, String workFlowName, String workFlowVersion, String workFlowSpace, Long projectId) {
        return checkAlertRepository.findByTopicAndWorkflowInfo(topic, workFlowName, workFlowVersion, workFlowSpace, projectId);
    }

    @Override
    public List<CheckAlert> findByRuleGroup(RuleGroup ruleGroupInDb) {
        return checkAlertRepository.findByRuleGroup(ruleGroupInDb);
    }

    @Override
    public CheckAlert findByProjectAndWorkflowNameAndTopic(Project project, String workflowName, String topic) {
        return checkAlertRepository.findByProjectAndWorkflowNameAndTopic(project.getId(), workflowName, topic);
    }

    @Override
    public CheckAlert findLowestVersionByProjectAndTopic(Long projectId, String topic) {
        return checkAlertRepository.findLowestVersionByProjectAndTopic(projectId, topic);
    }

    @Override
    public int countByProjectAndTopic(Long projectId, String topic) {
        return checkAlertRepository.countByProjectAndTopic(projectId, topic);
    }

    @Override
    public List<CheckAlert> findByProjectAndWorkflowNameAndTopics(Project project, String workflowName, List<String> topics) {
        return checkAlertRepository.findByProjectAndWorkflowNameAndTopics(project.getId(), workflowName, topics);
    }

    @Override
    public Long selectMateCheckAlert(String topic, String workFlowName, String version, Long projectId) {
        return checkAlertRepository.selectMateCheckAlert(topic, workFlowName, version, projectId);
    }

    @Override
    public List<Map<String,Object>> getDeduplicationField(Long projectId) {
        return checkAlertRepository.getDeduplicationField(projectId);
    }

    @Override
    public Page<CheckAlert> checkAlertQuery(String topic, String alertTable, String workFlowSpace, String workFlowProject, String workFlowName,
                                            String nodeName, String createUser, String modifyUser, String startCreateTime, String endCreateTime, String startModifyTime, String endModifyTime, Long projectId, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return checkAlertRepository.checkAlertQuery(topic, alertTable, workFlowSpace, workFlowProject, workFlowName,
                nodeName, createUser, modifyUser, startCreateTime, endCreateTime, startModifyTime, endModifyTime, projectId, pageable);
    }

    @Override
    public List<CheckAlert> saveAll(List<CheckAlert> checkAlerts) {
        return checkAlertRepository.saveAll(checkAlerts);
    }
}
