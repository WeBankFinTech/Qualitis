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

import com.webank.wedatasphere.qualitis.project.constant.ProjectTypeEnum;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RuleRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Repository
public class RuleDaoImpl implements RuleDao {

    @Autowired
    private RuleRepository ruleRepository;

    @Override
    public int countByProject(Project project) {
        return ruleRepository.countByProject(project);
    }

    @Override
    public List<Rule> findByProjectWithPage(Project project, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleRepository.findByProject(project, pageable).getContent();
    }

    @Override
    public List<Rule> findByProject(Project project) {
        return ruleRepository.findByProject(project);
    }

    @Override
    public List<Map<String, Object>> findSpecialInfoByProject(Project project) {
        return ruleRepository.findSpecialInfoByProject(project);
    }

    @Override
    public Page<Rule> findByConditionWithPage(Project project, String ruleName, String ruleCnName, Integer ruleTemplateId, String db, String table, Boolean ruleEnable
            , String createUser, String modifyUser, String startCreateTime, String endCreateTime, String startModifyTime, String endModifyTime, String ruleGroupName,
                                              String workFlowSpace, String workFlowProject, String workFlowName, String nodeName, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleRepository.findByConditionWithPage(project, ruleName, ruleCnName, ruleTemplateId, db, table, ruleEnable
                , createUser, modifyUser, startCreateTime, endCreateTime, startModifyTime, endModifyTime, ruleGroupName,
                workFlowSpace, workFlowProject, workFlowName, nodeName, pageable);
    }

    @Override
    public Rule findByProjectAndRuleName(Project project, String ruleName) {
        Rule rule = null;
        //1普通项目 2工作流项目
        if (ProjectTypeEnum.NORMAL_PROJECT.getCode().equals(project.getProjectType())) {
            rule = ruleRepository.findByProjectAndRuleName(project.getId(), ruleName);
        } else if (ProjectTypeEnum.WORKFLOW_PROJECT.getCode().equals(project.getProjectType())) {
            rule = ruleRepository.findHighestWorkFlowVersion(project.getId(), ruleName);
        }

        return rule;
    }

    @Override
    public Rule findHighestVersionByProjectAndWorkFlowName(Project project, String workflowName, String ruleName) {
        return ruleRepository.findHighestVersionByProjectAndWorkFlowName(project.getId(), workflowName, ruleName);
    }


    @Override
    public List<Rule> findByProjectAndWorkflowNameAndRuleNames(Project project, String workflowName, List<String> ruleNames) {
        return ruleRepository.findByProjectAndWorkflowNameAndRuleNames(project.getId(), workflowName, ruleNames);
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
    public void deleteById(Long ruleId) {
        ruleRepository.deleteById(ruleId);
    }

    @Override
    public List<Rule> findByIds(List<Long> ruleIds) {
        return ruleRepository.findAllById(ruleIds);
    }

    @Override
    public int countByRuleGroup(RuleGroup ruleGroup) {
        return ruleRepository.countByRuleGroup(ruleGroup);
    }

    @Override
    public List<Rule> findByRuleGroup(RuleGroup ruleGroup) {
        return ruleRepository.findByRuleGroup(ruleGroup);
    }

    @Override
    public List<Rule> findByRuleGroupWithPage(int page, int size, RuleGroup ruleGroup, Long templateId, String name, String cnName, List<String> cols, Integer ruleType) {
        Sort sort = Sort.by(Sort.Direction.DESC, "ruleNo");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleRepository.findByRuleGroupWithPage(ruleGroup, templateId, name, cnName, cols, ruleType, pageable).getContent();
    }

    @Override
    public Long countByRuleGroupWithPage(RuleGroup ruleGroup, Long templateId, String name, String cnName, List<String> cols, Integer ruleType) {
        return ruleRepository.countByRuleGroupWithPage(ruleGroup, templateId, name, cnName, cols, ruleType);
    }

    @Override
    public List<Rule> findByRuleGroupAndFileOutNameWithPage(int page, int size, RuleGroup ruleGroup, Long templateId, String name, String cnName, List<String> cols, Integer ruleType) {
        Sort sort = Sort.by(Sort.Direction.DESC, "ruleNo");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleRepository.findByRuleGroupAndFileOutNameWithPage(ruleGroup, templateId, name, cnName, cols, ruleType, pageable).getContent();
    }

    @Override
    public Long countByRuleGroupAndFileOutName(RuleGroup ruleGroup, Long templateId, String name, String cnName, List<String> cols, Integer ruleType) {
        return ruleRepository.countByRuleGroupAndFileOutNameWithPage(ruleGroup, templateId, name, cnName, cols, ruleType);
    }

    @Override
    public List<Rule> findByTemplate(Template templateInDb) {
        return ruleRepository.findByTemplate(templateInDb);
    }

    @Override
    public List<Rule> getDeployExecutionParameters(Long projectId, String name) {
        return ruleRepository.getDeployExecutionParameters(projectId, name);
    }

    @Override
    public Page<Rule> findRuleByDataSource(String clusterName, String dbName, String tableName, String colName, String user, Long ruleTemplateId, Integer relationObjectType, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BigInteger> idsPage = ruleRepository.findRuleByDataSource(clusterName, dbName, tableName, colName, user, ruleTemplateId, relationObjectType, pageable);

        if (CollectionUtils.isNotEmpty(idsPage.getContent())) {
            List<Long> ids = idsPage.getContent().stream().map(BigInteger::longValue).collect(Collectors.toList());
            List<Rule> ruleList = ruleRepository.findAllById(ids);
            return new PageImpl<>(ruleList, pageable, idsPage.getTotalElements());
        }

        return new PageImpl<>(Collections.emptyList(), pageable, idsPage.getTotalElements());
    }

    @Override
    public int countByProjectAndRuleName(String ruleName, Long projectId) {
        return ruleRepository.countByProjectAndRuleName(ruleName, projectId);
    }

    @Override
    public Long selectMateRule(String ruleName, String workFlowName, String workFlowVersion, Long projectId) {
        return ruleRepository.selectMateRule(ruleName, workFlowName, workFlowVersion, projectId);
    }

    @Override
    public Rule findMinWorkFlowVersionRule(String ruleName, Long projectId) {
        return ruleRepository.findLowestWorkFlowVersion(projectId, ruleName);
    }

    @Override
    public List<Rule> findAllById(List<Long> ruleIds) {
        return ruleRepository.findAllById(ruleIds);
    }

    @Override
    public List<Rule> saveRules(List<Rule> rules) {
        return ruleRepository.saveAll(rules);
    }

    @Override
    public List<Rule> findExistStandardVaule(Long templateId, Long projectId) {
        return ruleRepository.findExistStandardVaule(templateId, projectId);
    }

    @Override
    public List<Rule> findCustomRuleTypeByProject(Integer ruleType, Long projectId) {
        return ruleRepository.findCustomRuleTypeByProject(ruleType, projectId);
    }

    @Override
    public List<Map<String, Object>> findWorkFlowFiled(Long projectId) {
        return ruleRepository.findWorkFlowFiled(projectId);
    }

}
