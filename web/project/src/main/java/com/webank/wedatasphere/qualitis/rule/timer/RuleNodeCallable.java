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

package com.webank.wedatasphere.qualitis.rule.timer;

import com.webank.wedatasphere.qualitis.checkalert.dao.CheckAlertDao;
import com.webank.wedatasphere.qualitis.checkalert.entity.CheckAlert;
import com.webank.wedatasphere.qualitis.checkalert.service.CheckAlertService;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.CopyRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.RuleNodeRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddFileRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.AddMultiSourceRuleRequest;
import com.webank.wedatasphere.qualitis.rule.service.impl.RuleNodeServiceImpl;
import com.webank.wedatasphere.qualitis.rule.service.MultiSourceRuleService;
import com.webank.wedatasphere.qualitis.rule.service.CustomRuleService;
import com.webank.wedatasphere.qualitis.rule.service.FileRuleService;
import com.webank.wedatasphere.qualitis.rule.service.RuleNodeService;
import com.webank.wedatasphere.qualitis.rule.service.RuleService;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * @author allenzhou
 */
public class RuleNodeCallable implements Callable<List<Exception>> {

    private Project project;
    private RuleGroup ruleGroup;
    private RuleGroup targetRuleGroup;
    private ObjectMapper objectMapper;

    private RuleDao ruleDao;
    private CheckAlertDao checkAlertDao;

    private RuleService ruleService;
    private FileRuleService fileRuleService;
    private CustomRuleService customRuleService;
    private MultiSourceRuleService multiSourceRuleService;

    private List<Rule> rules;
    private List<CheckAlert> checkAlerts;
    private List<RuleNodeRequest> ruleNodeRequestList;
    private CopyRuleRequest request;

    private CountDownLatch latch;
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleNodeCallable.class);

    public RuleNodeCallable(List<RuleNodeRequest> ruleNodeRequestList, List<Rule> rules, Project projectInDb, RuleGroup ruleGroupInDb, ObjectMapper objectMapper, CountDownLatch latch) {
        this.ruleNodeRequestList = ruleNodeRequestList;
        this.objectMapper = objectMapper;
        this.ruleGroup = ruleGroupInDb;
        this.project = projectInDb;
        this.rules = rules;
        this.latch = latch;
    }

    public RuleNodeCallable(List<RuleNodeRequest> ruleNodeRequestList, List<Rule> rules, List<CheckAlert> checkAlerts, CopyRuleRequest copyRuleRequest, RuleService ruleService
        , CustomRuleService customRuleService, MultiSourceRuleService multiSourceRuleService, FileRuleService fileRuleService, RuleDao ruleDao, CheckAlertDao checkAlertDao
        , RuleGroup targetRuleGroup, Project targetProject, ObjectMapper objectMapper, CountDownLatch latch) {
        this.multiSourceRuleService = multiSourceRuleService;
        this.ruleNodeRequestList = ruleNodeRequestList;
        this.customRuleService = customRuleService;
        this.targetRuleGroup = targetRuleGroup;
        this.fileRuleService = fileRuleService;
        this.checkAlertDao = checkAlertDao;
        this.objectMapper = objectMapper;
        this.request = copyRuleRequest;
        this.ruleService = ruleService;
        this.checkAlerts = checkAlerts;
        this.project = targetProject;
        this.ruleDao = ruleDao;
        this.latch = latch;
        this.rules = rules;
    }

    @Override
    public List<Exception> call() {
        List<Exception> returnExceptions = new ArrayList<>();
        try {
            LOGGER.info(Thread.currentThread().getName() + " start to operate node rules.");

            if (CollectionUtils.isNotEmpty(this.ruleNodeRequestList)) {
                for (RuleNodeRequest ruleNodeRequest : this.ruleNodeRequestList) {
                    try {
                        SpringContextHolder.getBean(RuleNodeServiceImpl.class).importRule(ruleNodeRequest, this.project, this.ruleGroup, this.objectMapper);
                    } catch (Exception e) {
                        LOGGER.error("Failed to operate node rule. Rule info: {}", ruleNodeRequest.getRuleObject(), e);
                        returnExceptions.add(e);
                    }
                }
            } else if (CollectionUtils.isNotEmpty(this.checkAlerts)) {
                copyCheckAlert(returnExceptions, this.project);
            } else if (CollectionUtils.isNotEmpty(this.rules)) {
                copyRule(returnExceptions);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to operate node rule, caused by: {}", e.getMessage(), e);
            returnExceptions.add(e);
        } finally {
            latch.countDown();
        }
        return returnExceptions;
    }

    private void copyCheckAlert(List<Exception> returnExceptions, Project project) {
        try {
            int totalFinish = 0;
            for (CheckAlert checkAlert : checkAlerts) {
                if (StringUtils.isNotBlank(request.getWorkFlowName()) && StringUtils.isNotBlank(request.getVersion())) {
                    Long checkAlertInDbId = checkAlertDao.selectMateCheckAlert(checkAlert.getTopic(), request.getWorkFlowName(), request.getVersion(), targetRuleGroup.getProjectId());
                    CheckAlert marryCheckAlert = null;
                    if (checkAlertInDbId != null) {
                        marryCheckAlert = checkAlertDao.findById(checkAlertInDbId);
                    }
                    if (null == marryCheckAlert) {
                        String checkAlertObjectStr = objectMapper.writeValueAsString(checkAlert);
                        CheckAlert checkAlertObject = objectMapper.readValue(checkAlertObjectStr, CheckAlert.class);

                        checkAlertObject.setId(null);
                        checkAlertObject.setProject(project);
                        checkAlertObject.setRuleGroup(targetRuleGroup);
                        if (StringUtils.isNotEmpty(request.getNodeName())) {
                            checkAlertObject.setNodeName(request.getNodeName());
                        }
                        checkAlertObject.setWorkFlowVersion(request.getVersion());
                        checkAlertObject.setWorkFlowName(request.getWorkFlowName());
                        LOGGER.info("Success to copy check alert: " + checkAlertDao.save(checkAlertObject).toString());
                        int total = checkAlertDao.countByProjectAndTopic(project.getId(), checkAlertObject.getTopic());
                        if (total > QualitisConstants.DSS_NODE_VERSION_NUM) {
                            CheckAlert lowestCheckAlert = checkAlertDao.findLowestVersionByProjectAndTopic(project.getId(), checkAlertObject.getTopic());

                            if (lowestCheckAlert != null) {
                                LOGGER.info("Start to delete lowest version check alert. {}", lowestCheckAlert.toString());
                                checkAlertDao.delete(lowestCheckAlert);
                                LOGGER.info("Success to delete lowest version check alert.");
                            }
                        }
                    }
                    totalFinish++;
                }
            }
            if (totalFinish != checkAlerts.size()) {
                throw new UnExpectedRequestException("{&COPY_RULE_FAILED}");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to copy node check alert.", e);
            returnExceptions.add(e);
        }
    }

    private void copyRule(List<Exception> returnExceptions) {
        try {
            int totalFinish = 0;
            for (Rule rule : rules) {
                //根据ruleName、工作流名称、工作流版本号，去查Rule
                if (StringUtils.isNotBlank(request.getWorkFlowName()) && StringUtils.isNotBlank(request.getVersion())) {
                    Long ruleInDbId = ruleDao
                        .selectMateRule(rule.getName(), request.getWorkFlowName(), request.getVersion(), targetRuleGroup.getProjectId());
                    Rule marryRule = null;
                    if (ruleInDbId != null) {
                        marryRule = ruleDao.findById(ruleInDbId);
                    }
                    switch (rule.getRuleType().intValue()) {
                        case 1:
                            if (null == marryRule) {
                                AddRuleRequest addRuleRequest = SpringContextHolder.getBean(RuleNodeService.class)
                                    .constructSingleRequest(rule, targetRuleGroup, request.getWorkFlowName());
                                ruleService.addRuleForOuter(addRuleRequest, request.getCreateUser());
                            }
                            totalFinish++;
                            break;
                        case 2:
                            if (null == marryRule) {
                                AddCustomRuleRequest addCustomRuleRequest = SpringContextHolder.getBean(RuleNodeService.class)
                                    .constructCustomRequest(rule, targetRuleGroup, request.getWorkFlowName());
                                customRuleService.addRuleForOuter(addCustomRuleRequest, request.getCreateUser());
                            }
                            totalFinish++;
                            break;
                        case 3:
                            if (null == marryRule) {
                                AddMultiSourceRuleRequest addMultiSourceRuleRequest = SpringContextHolder.getBean(RuleNodeService.class)
                                    .constructMultiRequest(rule, targetRuleGroup, request.getWorkFlowName());
                                addMultiSourceRuleRequest.setLoginUser(request.getCreateUser());
                                multiSourceRuleService.addRuleForOuter(addMultiSourceRuleRequest, false);
                            }
                            totalFinish++;
                            break;
                        case 4:
                            if (null == marryRule) {
                                AddFileRuleRequest addFileRuleRequest = SpringContextHolder.getBean(RuleNodeService.class)
                                    .constructFileRequest(rule, targetRuleGroup, request.getWorkFlowName());
                                fileRuleService.addRuleForOuter(addFileRuleRequest, request.getCreateUser());
                            }
                            totalFinish++;
                            break;
                        default:
                            break;
                    }

                } else {
                    totalFinish = SpringContextHolder.getBean(RuleNodeService.class)
                        .handleParameterIsNull(request, totalFinish, targetRuleGroup, rule);
                }
            }
            if (totalFinish != rules.size()) {
                throw new UnExpectedRequestException("{&COPY_RULE_FAILED}");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to copy node rule.", e);
            returnExceptions.add(e);
        }
    }
}
