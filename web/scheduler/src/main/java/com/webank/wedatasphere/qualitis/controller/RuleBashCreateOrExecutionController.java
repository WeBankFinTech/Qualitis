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

package com.webank.wedatasphere.qualitis.controller;

import com.webank.wedatasphere.qualitis.constant.InvokeTypeEnum;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.request.GroupExecutionRequest;
import com.webank.wedatasphere.qualitis.request.RuleBashRequest;
import com.webank.wedatasphere.qualitis.response.ApplicationTaskSimpleResponse;
import com.webank.wedatasphere.qualitis.response.CreateAndSubmitResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.RuleBashResponse;
import com.webank.wedatasphere.qualitis.rule.config.RuleConfig;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.request.AbstractCommonRequest;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import com.webank.wedatasphere.qualitis.rule.service.RuleDataSourceService;
import com.webank.wedatasphere.qualitis.service.AddDirector;
import com.webank.wedatasphere.qualitis.service.CreateAndExecutionService;
import com.webank.wedatasphere.qualitis.service.OuterExecutionService;
import com.webank.wedatasphere.qualitis.timer.RuleBashThreadResponse;
import com.webank.wedatasphere.qualitis.timer.RuleBashUpdaterCallable;
import com.webank.wedatasphere.qualitis.timer.RuleBashUpdaterThreadFactory;
import com.webank.wedatasphere.qualitis.util.BashUtils;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.JexlUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author allenzhou
 */
@Path("api/v1/projector/rule/bash")
public class RuleBashCreateOrExecutionController {
    @Autowired
    private RuleConfig ruleConfig;

    @Autowired
    private AddDirector addDirector;

    @Autowired
    private RuleDao ruleDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private RuleGroupDao ruleGroupDao;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private OuterExecutionService outerExecutionService;

    @Autowired
    private CreateAndExecutionService createAndExecutionService;

    @Autowired
    private RuleDataSourceService ruleDataSourceService;

    @Autowired
    private ApplicationContext applicationContext;


    private static final String CMD = "-cmd";
    private static final String RULE_NAME = "--rule-name";
    private static final String RULE_DETAIL = "--rule-detail";
    private static final String RULE_CN_NAME = "--rule-cnname";
    private static final String RULE_PROXY_USER = "--proxy-user";

    private static final String ERROR_MESSAGE_PREFIX = "> ";
    private static final String NOTE_MESSAGE_PREFIX = "# ";

    private static final ThreadPoolExecutor POOL = new ThreadPoolExecutor(100,
            Integer.MAX_VALUE,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1000),
            new RuleBashUpdaterThreadFactory(),
            new ThreadPoolExecutor.DiscardPolicy());

    private static final Pattern PROJECT_RULE_NAME_PATTERN = Pattern.compile("^\\w+$");

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleBashCreateOrExecutionController.class);

    private HttpServletRequest httpServletRequest;

    public RuleBashCreateOrExecutionController(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleBashResponse> add(RuleBashRequest request) throws UnExpectedRequestException {
        try {
            RuleGroup ruleGroupInDb = null;
            if (request.getRuleGroupId() != null) {
                ruleGroupInDb = ruleGroupDao.findById(request.getRuleGroupId());
            }
            String loginUser = HttpUtils.getUserName(httpServletRequest);

            StringBuffer errorLines = new StringBuffer();
            List<Long> savedRuleIds = new ArrayList<>(8);
            RuleGroup currentRuleGroup = initBashByConcurrent(ruleGroupInDb, request, savedRuleIds, errorLines, loginUser);
            RuleBashResponse ruleBashResponse = new RuleBashResponse();
            if (currentRuleGroup != null) {
                List<Rule> savedRules = ruleDao.findByIds(savedRuleIds);
                List<Rule> rules = ruleDao.findByRuleGroup(currentRuleGroup);
                LOGGER.info("Already exist rules: {}", Arrays.toString(rules.toArray()));
                LOGGER.info("Saved rules in bash node: {}", Arrays.toString(savedRules.toArray()));
                rules.removeAll(savedRules);
                if (CollectionUtils.isNotEmpty(rules)) {
                    LOGGER.info("Start to disable rules not in bash node.");
                    rules = rules.stream().map(rule -> {
                        rule.setEnable(false);
                        return rule;
                    }).collect(Collectors.toList());
                    ruleDao.saveRules(rules);
                }
                ruleBashResponse = new RuleBashResponse(savedRules);
                if (ruleGroupInDb == null) {
                    ruleBashResponse.setRuleGroupId(currentRuleGroup.getId());
                }
            }
            if (ruleGroupInDb != null) {
                ruleBashResponse.setRuleGroupId(ruleGroupInDb.getId());
            }

            if (StringUtils.isNotEmpty(errorLines.toString())) {
                String successBashContent = StringUtils.isNotEmpty(ruleBashResponse.getBashContent()) ? ruleBashResponse.getBashContent() : "";
                ruleBashResponse.setBashContent(successBashContent + "\n" + errorLines.toString());
                return new GeneralResponse<>("5375", "{&ADD_RULE_PARTIAL_FAILED}", ruleBashResponse);
            }
            return new GeneralResponse<>("200", "{&ADD_RULE_SUCCESSFULLY}", ruleBashResponse);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to add rule. caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_ADD_RULE}", null);
        }
    }

    private RuleGroup initBashByConcurrent(RuleGroup ruleGroupInDb, RuleBashRequest request, List<Long> savedRuleIds, StringBuffer errorLines, String loginUser)
            throws UnExpectedRequestException, ExecutionException, InterruptedException {
        // Check request
        RuleBashRequest.checkRequest(request);

        if (StringUtils.isEmpty(loginUser)) {
            throw new UnExpectedRequestException("User not logged in qualitis.");
        }

        addDirector.setUserName(loginUser);

        String templateFunctions = request.getTemplateFunctions();
        LOGGER.info(loginUser + " start to use qualitis bash, content: [" + templateFunctions + "]");

        Long projectId = request.getProjectId();
        Project project = projectDao.findById(projectId);
        if (project == null) {
            throw new UnExpectedRequestException("Project {&DOES_NOT_EXIST}");
        }
        if (ruleGroupInDb == null) {
            ruleGroupInDb = ruleGroupDao.saveRuleGroup(new RuleGroup("Group_" + UUID.randomUUID().toString().replace("-", ""), project.getId()));
        }
        // Successfull save rule IDs. Remove line of rule bash syntax error line.
        List<String> lines = Arrays.asList(request.getTemplateFunctions().split("\n"));
        Map<String, Map<String, StringBuffer>> lineWithOpt = new HashMap<>(lines.size());
        removeLinesOfSameRuleBash(lines, lineWithOpt, errorLines);

        // Concurrent handle remainning lines that must be add or modify.
        if (lines.size() != lineWithOpt.size()) {
            throw new UnExpectedRequestException("The remaining lines not equal opt map size.");
        }
        createWithThreadPools(lines, lineWithOpt, errorLines, savedRuleIds, ruleGroupInDb, project, loginUser, request.getWorkFlowName(), request.getWorkFlowVersion(), request.getWorkFlowSpace(), request.getNodeName());

        return ruleGroupInDb;
    }

    private void createWithThreadPools(List<String> lines, Map<String, Map<String, StringBuffer>> lineWithOpt, StringBuffer errorLines
            , List<Long> savedRuleIds, RuleGroup ruleGroupInDb, Project project, String loginUser, String workFlowName, String workFlowVersion, String workFlowSpace, String nodeName) throws ExecutionException, InterruptedException {
        int total = lines.size();
        int updateThreadSize = total / ruleConfig.getRuleUpdateSize() + 1;
        if (total % ruleConfig.getRuleUpdateSize() == 0) {
            updateThreadSize = total / ruleConfig.getRuleUpdateSize();
        }
        CountDownLatch latch = new CountDownLatch(updateThreadSize);

        List<Future<RuleBashThreadResponse>> execptionFutures = new ArrayList<>(updateThreadSize);

        for (int indexThread = 0; total > 0 && indexThread < total; indexThread += ruleConfig.getRuleUpdateSize()) {
            if (indexThread + ruleConfig.getRuleUpdateSize() < total) {
                List<String> currentLineList = lines.subList(indexThread, indexThread + ruleConfig.getRuleUpdateSize());

                Future<RuleBashThreadResponse> exceptionFuture = POOL.submit(new RuleBashUpdaterCallable(applicationContext.getBean(AddDirector.class), createAndExecutionService, project, ruleGroupInDb
                        , ruleDao, currentLineList.stream().filter(lineWithOpt::containsKey).collect(Collectors.toMap(Function.identity(), lineWithOpt::get, (oValue, nValue) -> nValue)), loginUser, workFlowName, workFlowVersion, workFlowSpace, nodeName, latch));
                execptionFutures.add(exceptionFuture);
            } else {
                List<String> currentLineList = lines.subList(indexThread, total);
                Future<RuleBashThreadResponse> exceptionFuture = POOL.submit(new RuleBashUpdaterCallable(applicationContext.getBean(AddDirector.class), createAndExecutionService, project, ruleGroupInDb
                        , ruleDao, currentLineList.stream().filter(lineWithOpt::containsKey).collect(Collectors.toMap(Function.identity(), lineWithOpt::get, (oValue, nValue) -> nValue)), loginUser, workFlowName, workFlowVersion, workFlowSpace, nodeName, latch));
                execptionFutures.add(exceptionFuture);
            }
            updateThreadSize--;
        }

        if (total > 0 && updateThreadSize == 0) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted!", e);
                Thread.currentThread().interrupt();
                throw new InterruptedException(e.getMessage());
            }
        }

        for (Future<RuleBashThreadResponse> exceptionFuture : execptionFutures) {
            RuleBashThreadResponse ruleBashThreadResponse = exceptionFuture.get();
            if (ruleBashThreadResponse != null) {
                if (StringUtils.isNotEmpty(ruleBashThreadResponse.getExceptionMess().toString())) {
                    errorLines.append(ruleBashThreadResponse.getExceptionMess().toString());
                }
                if (CollectionUtils.isNotEmpty(ruleBashThreadResponse.getRuleIdList())) {
                    savedRuleIds.addAll(ruleBashThreadResponse.getRuleIdList());
                }
            }
        }

    }

    private void removeLinesOfSameRuleBash(List<String> lines, Map<String, Map<String, StringBuffer>> lineWithOpt, StringBuffer errorLines) {
        int ruleNo = 0;
        for (Iterator<String> iterator = lines.iterator(); iterator.hasNext(); ) {
            String line = iterator.next();
            if (line.startsWith(ERROR_MESSAGE_PREFIX) || line.startsWith(NOTE_MESSAGE_PREFIX) || line.length() == 0) {
                iterator.remove();
                continue;
            }
            try {
                if (!line.contains(CMD)) {
                    iterator.remove();
                    throw new UnExpectedRequestException("CMD is necessary.");
                }

                if (!line.contains(RULE_NAME)) {
                    iterator.remove();
                    throw new UnExpectedRequestException("Rule name is necessary.");
                }

                if (!line.endsWith(SpecCharEnum.DIVIDER.getValue())) {
                    iterator.remove();
                    throw new UnExpectedRequestException("Command must end with ';'.");
                }

                // Get rule name, rule cn name, rule detail, proxy user if exists.
                Map<String, StringBuffer> opts = new HashMap<>(5);

                getOptValues(opts, line);
                String ruleName = opts.get(RULE_NAME).toString();
                Matcher matcher = PROJECT_RULE_NAME_PATTERN.matcher(ruleName);

                boolean find = matcher.find();
                if (!find) {
                    iterator.remove();
                    throw new UnExpectedRequestException("Rule name is illegal.");
                }

                opts.put("ruleNo", new StringBuffer(String.valueOf(ruleNo++)));
                lineWithOpt.put(line, opts);
            } catch (UnExpectedRequestException e) {
                iterator.remove();
                LOGGER.error(e.getMessage(), e);
                errorLines.append(ERROR_MESSAGE_PREFIX).append(line).append("\n").append(ERROR_MESSAGE_PREFIX).append(e.getMessage()).append("\n");
            } catch (Exception e) {
                iterator.remove();
                LOGGER.error(e.getMessage(), e);
                errorLines.append(ERROR_MESSAGE_PREFIX).append(line).append("\n").append(ERROR_MESSAGE_PREFIX).append(e.getMessage()).append("\n");
            }
        }
    }

    @POST
    @Path("submit")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleBashResponse> submit(RuleBashRequest request) throws UnExpectedRequestException {
        try {
            RuleGroup ruleGroupInDb = null;
            if (request.getRuleGroupId() != null) {
                ruleGroupInDb = ruleGroupDao.findById(request.getRuleGroupId());
            }
            String loginUser = HttpUtils.getUserName(httpServletRequest);

            StringBuffer errorLines = new StringBuffer();
            RuleGroup currentRuleGroup = initBash(ruleGroupInDb, request, errorLines, loginUser);
            RuleBashResponse ruleBashResponse = new RuleBashResponse();

            if (StringUtils.isNotEmpty(errorLines.toString())) {
                ruleBashResponse.setBashContent(ruleBashResponse.getBashContent() + "\n" + errorLines.toString());
            }

            if (currentRuleGroup != null) {
                List<Rule> rules = ruleDao.findByRuleGroup(currentRuleGroup);
                ruleBashResponse = new RuleBashResponse(rules);
                // Submit rule by rule group ID.
                GroupExecutionRequest groupExecutionRequest = new GroupExecutionRequest(currentRuleGroup.getId(), StringUtils.isNotEmpty(request.getProxyUser()) ? request.getProxyUser() : loginUser
                        , loginUser, request.getNodeName());
                GeneralResponse<ApplicationTaskSimpleResponse> generalResponse = (GeneralResponse<ApplicationTaskSimpleResponse>) outerExecutionService.groupExecution(groupExecutionRequest, InvokeTypeEnum.FLOW_API_INVOKE.getCode(), loginUser);
                LOGGER.info(generalResponse != null ? generalResponse.toString() : "General application task simple response is empty.");
                return new GeneralResponse<>("200", "{&SUCCESS_ASYNC_SUBMIT_TASK}", ruleBashResponse);
            }

            return new GeneralResponse<>("500", "{&FAILED_TO_SUBMIT_TASK}", ruleBashResponse);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to add rule. caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_SUBMIT_TASK}", null);
        }
    }

    @POST
    @Path("get")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleBashResponse> get(RuleBashRequest request) throws UnExpectedRequestException {
        try {
            String loginUser = HttpUtils.getUserName(httpServletRequest);

            if (StringUtils.isEmpty(loginUser)) {
                throw new UnExpectedRequestException("{&LOGIN_FAILED}");
            }

            RuleGroup ruleGroupInDb = null;
            if (request.getRuleGroupId() != null) {
                ruleGroupInDb = ruleGroupDao.findById(request.getRuleGroupId());
                LOGGER.info(loginUser + " get bash exists rule group: " + ruleGroupInDb.toString());
            }

            if (ruleGroupInDb != null) {
                List<Rule> rules = ruleDao.findByRuleGroup(ruleGroupInDb).stream().filter(rule -> rule.getEnable()).collect(Collectors.toList());
                Collections.sort(rules, new Comparator<Rule>() {
                    @Override
                    public int compare(Rule front, Rule back) {
                        if (front.getRuleNo() == null || back.getRuleNo() == null) {
                            return 0;
                        }
                        return front.getRuleNo().compareTo(back.getRuleNo());
                    }
                });
                RuleBashResponse ruleBashResponse = new RuleBashResponse(rules);
                return new GeneralResponse<>("200", "{&GET_RULE_DETAIL_SUCCESSFULLY}", ruleBashResponse);
            }

            return new GeneralResponse<>("200", "{&NO_RULE_CAN_BE_EXECUTED}", null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to add rule. caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_SUBMIT_TASK}", null);
        }
    }

    private RuleGroup initBash(RuleGroup ruleGroupInDb, RuleBashRequest request, StringBuffer errorLines, String loginUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check request
        RuleBashRequest.checkRequest(request);

        if (StringUtils.isEmpty(loginUser)) {
            throw new UnExpectedRequestException("User not logged in qualitis.");
        }

        addDirector.setUserName(loginUser);

        String templateFunctions = request.getTemplateFunctions();
        LOGGER.info(loginUser + " start to use qualitis bash, content: [" + templateFunctions + "]");

        Long projectId = request.getProjectId();
        Project project = projectDao.findById(projectId);
        if (project != null) {
            addDirector.setProject(project);
            addDirector.setProjectName(project.getName());
        } else {
            throw new UnExpectedRequestException("Project {&DOES_NOT_EXIST}");
        }

        // Preprocessing template functions.
        String[] lines = templateFunctions.split("\n");
        List<Rule> savedRules = new ArrayList<>(8);
        ruleGroupInDb = handleLinesForRule(ruleGroupInDb, errorLines, loginUser, request.getWorkFlowName(), request.getWorkFlowVersion(), request.getWorkFlowSpace(), request.getNodeName(), project, lines, savedRules);
        if (ruleGroupInDb != null) {
            List<Rule> existRules = ruleDao.findByRuleGroup(ruleGroupInDb);
            LOGGER.info("Already exist rules: {}", Arrays.toString(existRules.toArray()));
            LOGGER.info("Saved rules in bash node: {}", Arrays.toString(savedRules.toArray()));
            existRules.removeAll(savedRules);
            if (CollectionUtils.isNotEmpty(existRules)) {
                LOGGER.info("Start to delete rules not in bash node.");
                projectService.deleteAllRules(existRules, loginUser);
            }
        }
        return ruleGroupInDb;
    }

    private RuleGroup handleLinesForRule(RuleGroup ruleGroupInDb, StringBuffer errorLines, String loginUser, String workflowName, String workFlowVersion, String workFlowSpace, String nodeName, Project project, String[] lines, List<Rule> savedRules) {
        // Process multiple lines of instructions concurrently

        for (String line : lines) {
            if (line.startsWith(ERROR_MESSAGE_PREFIX) || line.startsWith(NOTE_MESSAGE_PREFIX) || line.length() == 0) {
                continue;
            }
            try {
                if (!line.contains(RULE_NAME)) {
                    throw new UnExpectedRequestException("Rule name is necessary.");
                }

                if (!line.contains(CMD)) {
                    throw new UnExpectedRequestException("CMD is necessary.");
                }

                if (!line.endsWith(SpecCharEnum.DIVIDER.getValue())) {
                    throw new UnExpectedRequestException("Command must end with ';'.");
                }

                // Get rule name, rule cn name, rule detail, proxy user if exists.
                Map<String, StringBuffer> opts = new HashMap<>(5);

                getOptValues(opts, line);

                String ruleName = opts.get(RULE_NAME).toString();
                Matcher matcher = PROJECT_RULE_NAME_PATTERN.matcher(ruleName);
                boolean find = matcher.find();
                if (!find) {
                    throw new UnExpectedRequestException("Rule name is illegal.");
                }
                addDirector.setRuleName(ruleName);
                Rule rule = ruleDao.findByProjectAndRuleName(project, ruleName);
                if (rule != null) {
                    if (StringUtils.isNotEmpty(rule.getBashContent()) && rule.getBashContent().equals(line)) {
                        if (ruleGroupInDb == null) {
                            errorLines.append(ERROR_MESSAGE_PREFIX).append(line).append("\n");
                            errorLines.append(ERROR_MESSAGE_PREFIX).append(rule.getName() + " {&ALREADY_EXIST}").append("\n");
                            continue;
                        }
                        savedRules.add(rule);
                        continue;
                    }
                    addDirector.setRule(rule);
                }
                addDirector.setRuleDetail(opts.get(RULE_DETAIL).toString());
                addDirector.setRuleCnName(opts.get(RULE_CN_NAME).toString());
                addDirector.setProxyUser(opts.get(RULE_PROXY_USER).toString());
                LOGGER.info("User from bash begin to create rule. Name:[" + ruleName + "]");
                Map<String, Object> map = new HashMap<>(1);
                map.put("addDirector", addDirector);
                CreateAndSubmitResponse response = new CreateAndSubmitResponse();
                AbstractCommonRequest abstrackAddRequest = (AbstractCommonRequest) JexlUtil.executeExpression("addDirector." + opts.get(CMD).toString() + ".returnRequest()", map);
                RuleResponse ruleResponse = createAndExecutionService.addOrModifyRule(addDirector, loginUser, abstrackAddRequest, line, workflowName, workFlowVersion, workFlowSpace, nodeName, ruleGroupInDb, response);
                if (ruleGroupInDb == null) {
                    ruleGroupInDb = ruleGroupDao.findById(ruleResponse.getRuleGroupId());
                }
                savedRules.add(ruleDao.findById(ruleResponse.getRuleId()));
            } catch (UnExpectedRequestException e) {
                LOGGER.error(e.getMessage(), e);
                errorLines.append(ERROR_MESSAGE_PREFIX).append(line).append("\n").append(ERROR_MESSAGE_PREFIX).append(e.getMessage()).append("\n");
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                errorLines.append(ERROR_MESSAGE_PREFIX).append(line).append("\n").append(ERROR_MESSAGE_PREFIX).append(e.getMessage()).append("\n");
            }
        }
        return ruleGroupInDb;
    }

    private void getOptValues(Map<String, StringBuffer> opts, String line) {
        opts.put(RULE_NAME, new StringBuffer(BashUtils.getCommonValue(RULE_NAME, line)));
        opts.put(RULE_DETAIL, new StringBuffer(BashUtils.getCommonValue(RULE_DETAIL, line)));
        opts.put(RULE_CN_NAME, new StringBuffer(BashUtils.getCommonValue(RULE_CN_NAME, line)));
        opts.put(RULE_PROXY_USER, new StringBuffer(BashUtils.getCommonValue(RULE_PROXY_USER, line)));

        int firstApostrophe = line.indexOf("'");
        int lastApostrophe = line.lastIndexOf("'");


        opts.put(CMD, new StringBuffer(line.substring(firstApostrophe + 1, lastApostrophe).trim()));

    }
}
