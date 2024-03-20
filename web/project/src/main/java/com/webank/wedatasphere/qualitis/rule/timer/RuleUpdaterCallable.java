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

import com.webank.wedatasphere.qualitis.scheduled.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.rule.request.AddFileRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddGroupRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyFileRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyGroupRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleRequest;
import com.webank.wedatasphere.qualitis.rule.service.FileRuleService;
import com.webank.wedatasphere.qualitis.rule.service.RuleService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * @author allenzhou
 */
public class RuleUpdaterCallable implements Callable<List<Exception>> {
    private RuleService ruleService;
    private FileRuleService fileRuleService;

    private String type;
    private String loginUser;
    private CountDownLatch latch;
    private List<AddGroupRuleRequest> addGroupRuleRequestList;
    private List<ModifyGroupRuleRequest> modifyGroupRuleRequestList;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleUpdaterCallable.class);

    public RuleUpdaterCallable(RuleService ruleService, FileRuleService fileRuleService,
        List<AddGroupRuleRequest> addGroupRuleRequests, String loginUser, CountDownLatch latch) {
        this.ruleService = ruleService;
        this.fileRuleService = fileRuleService;
        this.addGroupRuleRequestList = addGroupRuleRequests;
        this.loginUser = loginUser;
        this.latch = latch;
    }

    public RuleUpdaterCallable(RuleService ruleService, FileRuleService fileRuleService,
        List<ModifyGroupRuleRequest> modifyGroupRuleRequestList, String loginUser, CountDownLatch latch, String type) {
        this.ruleService = ruleService;
        this.fileRuleService = fileRuleService;
        this.modifyGroupRuleRequestList = modifyGroupRuleRequestList;
        this.loginUser = loginUser;
        this.latch = latch;
        this.type = type;
    }

    @Override
    public List<Exception> call() {
        List<Exception> returnExceptions = new ArrayList<>();
        try {
            LOGGER.info(Thread.currentThread().getName() + " start to update rules.");
            if (CollectionUtils.isNotEmpty(this.addGroupRuleRequestList)) {
                for (AddGroupRuleRequest request : this.addGroupRuleRequestList) {
                    try {
                        if (RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode().equals(request.getRuleType())) {
                            AddRuleRequest addRuleRequest = new AddRuleRequest();
                            BeanUtils.copyProperties(request, addRuleRequest);
                            ruleService.addRule(addRuleRequest, loginUser, true);
                        }
                        if (RuleTypeEnum.FILE_TEMPLATE_RULE.getCode().equals(request.getRuleType())) {
                            AddFileRuleRequest addFileRuleRequest = new AddFileRuleRequest();
                            BeanUtils.copyProperties(request, addFileRuleRequest);
                            addFileRuleRequest.setDatasource(request.getDatasource());

                            fileRuleService.addRule(addFileRuleRequest, loginUser, true);
                        }
                    } catch (Exception e) {
                        LOGGER.error("Failed to update, rule name: {}", request.getRuleName(), e);
                        returnExceptions.add(e);
                    }
                }
            } else if (CollectionUtils.isNotEmpty(this.modifyGroupRuleRequestList)) {
                for (ModifyGroupRuleRequest request : this.modifyGroupRuleRequestList) {
                    try {
                        if (RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode().equals(request.getRuleType())) {
                            if (null != request.getRuleId()) {
                                ModifyRuleRequest modifyRuleRequest = new ModifyRuleRequest();
                                BeanUtils.copyProperties(request, modifyRuleRequest);
                                ruleService.modifyRuleDetail(modifyRuleRequest, loginUser, true);
                            } else {
                                AddRuleRequest addRuleRequest = new AddRuleRequest();
                                BeanUtils.copyProperties(request, addRuleRequest);
                                ruleService.addRule(addRuleRequest, loginUser, true);
                            }
                        }
                        if (RuleTypeEnum.FILE_TEMPLATE_RULE.getCode().equals(request.getRuleType())) {
                            if (null != request.getRuleId()) {
                                ModifyFileRuleRequest modifyFileRuleRequest = new ModifyFileRuleRequest();
                                BeanUtils.copyProperties(request, modifyFileRuleRequest);
                                modifyFileRuleRequest.setDatasource(request.getDatasource());
                                fileRuleService.modifyRuleDetail(modifyFileRuleRequest, loginUser, true);
                            } else {
                                AddFileRuleRequest addFileRuleRequest = new AddFileRuleRequest();
                                BeanUtils.copyProperties(request, addFileRuleRequest);
                                addFileRuleRequest.setDatasource(request.getDatasource());
                                fileRuleService.addRule(addFileRuleRequest, loginUser, true);
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("Failed to update, rule name: {}", request.getRuleName(), e);
                        returnExceptions.add(e);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to update, caused by: {}", e.getMessage(), e);
        } finally {
            latch.countDown();
        }
        return returnExceptions;
    }
}
