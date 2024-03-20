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

package com.webank.wedatasphere.qualitis.timer;

import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.response.CreateAndSubmitResponse;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.request.AbstractCommonRequest;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import com.webank.wedatasphere.qualitis.service.AddDirector;
import com.webank.wedatasphere.qualitis.service.CreateAndExecutionService;
import com.webank.wedatasphere.qualitis.util.JexlUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * @author allenzhou
 */
public class RuleBashUpdaterCallable implements Callable<RuleBashThreadResponse> {
    private String loginUser;
    private String workFlowName;
    private String workFlowVersion;
    private String workFlowSpace;
    private String nodeName;

    private AddDirector addDirector;
    private CreateAndExecutionService createAndExecutionService;

    private RuleDao ruleDao;
    private Project project;
    private RuleGroup ruleGroup;
    private CountDownLatch latch;
    private Map<String, Map<String, StringBuffer>> lineWithOpt;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleBashUpdaterCallable.class);

    public RuleBashUpdaterCallable(AddDirector addDirector, CreateAndExecutionService createAndExecutionService, Project project
            , RuleGroup ruleGroup, RuleDao ruleDao, Map<String, Map<String, StringBuffer>> lineWithOpt, String loginUser, String workFlowName, String workFlowVersion, String workFlowSpace, String nodeName, CountDownLatch latch) {
        this.createAndExecutionService = createAndExecutionService;
        this.workFlowVersion = workFlowVersion;
        this.workFlowName = workFlowName;
        this.workFlowSpace = workFlowSpace;
        this.nodeName = nodeName;
        this.lineWithOpt = lineWithOpt;
        this.addDirector = addDirector;
        this.ruleGroup = ruleGroup;
        this.loginUser = loginUser;
        this.project = project;
        this.ruleDao = ruleDao;
        this.latch = latch;
    }

    @Override
    public RuleBashThreadResponse call() {
        RuleBashThreadResponse ruleBashThreadResponse = new RuleBashThreadResponse();
        try {
            if (CollectionUtils.isNotEmpty(this.lineWithOpt.keySet())) {
                for (Map.Entry<String, Map<String, StringBuffer>> entry : this.lineWithOpt.entrySet()) {
                    String ruleBash = entry.getKey();
                    Map<String, StringBuffer> value = entry.getValue();
                    try {
                        addDirector.setProject(project);
                        addDirector.setUserName(loginUser);
                        Map<String, StringBuffer> opts = value;
                        addDirector.setProjectName(project.getName());
                        addDirector.setRuleName(opts.get("--rule-name").toString());
                        int ruleNo = Integer.parseInt(opts.get("ruleNo").toString());
                        addDirector.setProxyUser(opts.get("--proxy-user").toString());
                        addDirector.setRuleDetail(opts.get("--rule-detail").toString());
                        addDirector.setRuleCnName(opts.get("--rule-cnname").toString());
                        Rule rule = ruleDao.findByProjectAndRuleName(project, opts.get("--rule-name").toString());
                        if (rule != null && rule.getRuleGroup().getId().equals(ruleGroup.getId())) {
                            addDirector.setRule(rule);
                            if (StringUtils.isNotEmpty(rule.getBashContent()) && rule.getBashContent().equals(ruleBash) && Boolean.TRUE.equals(rule.getEnable())) {
                                ruleBashThreadResponse.getRuleIdList().add(rule.getId());
                                rule.setRuleNo(ruleNo);
                                ruleDao.saveRule(rule);
                                continue;
                            }
                        }
                        LOGGER.info("User from bash begin to create rule. Name:[" + opts.get("--rule-name").toString() + "]");
                        Map<String, Object> map = new HashMap<>(1);
                        map.put("addDirector", addDirector);
                        CreateAndSubmitResponse response = new CreateAndSubmitResponse();
                        AbstractCommonRequest abstrackAddRequest = (AbstractCommonRequest) JexlUtil
                                .executeExpression("addDirector." + opts.get("-cmd").toString() + ".returnRequest()", map);

                        // Sort the line with rule no.
                        abstrackAddRequest.setRuleNo(ruleNo);
                        RuleResponse ruleResponse = createAndExecutionService.addOrModifyRule(addDirector, loginUser, abstrackAddRequest, ruleBash, workFlowName, workFlowVersion,workFlowSpace,nodeName, ruleGroup, response);
                        if (ruleResponse != null && ruleResponse.getRuleId() != null) {
                            ruleBashThreadResponse.getRuleIdList().add(ruleResponse.getRuleId());
                        }
                    } catch (Exception e) {
                        LOGGER.error("Failed to update, rule bash: {}", ruleBash, e);
                        ruleBashThreadResponse.getExceptionMess().append("> ").append(ruleBash).append("\n").append("> ").append(e.getMessage()).append("\n");
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to update, caused by: {}", e.getMessage(), e);
        } finally {
            latch.countDown();
        }
        return ruleBashThreadResponse;
    }
}
