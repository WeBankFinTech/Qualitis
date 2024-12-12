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

package com.webank.wedatasphere.qualitis.rule.controller;

import com.webank.wedatasphere.qualitis.checkalert.dao.CheckAlertDao;
import com.webank.wedatasphere.qualitis.checkalert.entity.CheckAlert;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.project.request.ParameterChecker;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.constant.GroupTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.exception.RuleLockException;
import com.webank.wedatasphere.qualitis.rule.request.*;
import com.webank.wedatasphere.qualitis.rule.response.RuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleGroupResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import com.webank.wedatasphere.qualitis.rule.service.RuleGroupService;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author howeye
 */

@Path("api/v1/projector/rule/group")
public class RuleGroupController {

    @Autowired
    private RuleDao ruleDao;

    @Autowired
    private RuleGroupDao ruleGroupDao;

    @Autowired
    private CheckAlertDao checkAlertDao;

    @Autowired
    private RuleGroupService ruleGroupService;

    private static final int ONE_HANDARD = 100;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleGroupController.class);

    @GET
    @Path("/{rule_group_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getRulesByRuleGroupId(@PathParam("rule_group_id") Long ruleGroupId) throws UnExpectedRequestException {
        try {
            // 查看ruleGroup是否存在
            RuleGroup ruleGroupInDb = ruleGroupDao.findById(ruleGroupId);
            if (ruleGroupInDb == null) {
                throw new UnExpectedRequestException(String.format("Rule Group: %s {&DOES_NOT_EXIST}", ruleGroupId));
            }

            if (GroupTypeEnum.CHECK_ALERT_GROUP.getCode().equals(ruleGroupInDb.getType())) {
                List<CheckAlert> checkAlertList = checkAlertDao.findByRuleGroup(ruleGroupInDb);
                if (CollectionUtils.isNotEmpty(checkAlertList)) {
                    List<Long> checkAlertIdList = checkAlertList.stream().map(checkAlert -> checkAlert.getId()).collect(Collectors.toList());
                    Map<String, List<Long>> responseMap = new HashMap<>(1);
                    responseMap.put("check_alert_id_list", checkAlertIdList);
                    return new GeneralResponse<>("200", "Succeed to find check alert rules by rule group id", responseMap);
                }
            }

            List<RuleResponse> ruleList = ruleDao.findByRuleGroup(ruleGroupInDb).stream().map(rule -> new RuleResponse(rule)).collect(Collectors.toList());
            return new GeneralResponse<>("200", "Succeed to find rules by rule group id",
                    new RuleGroupResponse(ruleGroupInDb, ruleList));
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get rules by rule group id. rule_group_id: {}, caused by: {}", ruleGroupId, e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_RULES_BY_RULE_GROUP}", null);
        }
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleGroupResponse> addRuleGroup(AddRuleGroupRequest addRuleGroupRequest) throws UnExpectedRequestException {
        try {
            addRuleGroupRequest.checkRequest();
            return new GeneralResponse<>("200", "{&SUCCESS_TO_ADD_RULE_GROUP}", ruleGroupService.addRuleGroup(addRuleGroupRequest));
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to add rule group, caused by: {}", e.getMessage());
            return new GeneralResponse<>("500", "{&FAILED_TO_ADD_RULE_GROUP}", null);
        }
    }

    @POST
    @Path("/modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleGroupResponse> modifyRuleGroup(ModifyRuleGroupRequest modifyRuleGroupRequest) throws UnExpectedRequestException {
        try {
            modifyRuleGroupRequest.checkRequest();
            return new GeneralResponse<>("200", "Succeed to modify rule group name by rule group id",
                ruleGroupService.modifyRuleGroupWithLock(modifyRuleGroupRequest));
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to modify rule group name by rule group id. rule_group_id: {}, caused by: {}", modifyRuleGroupRequest.getRuleGroupId()
                , e.getMessage());
            return new GeneralResponse<>("500", "{&FAILED_TO_MODIFY_RULE_GROUP_NAME}", null);
        }
    }

    @POST
    @Path("/option/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse queryRuleGroupList(RuleGroupPageRequest request) throws UnExpectedRequestException {
        try {
            request.checkRequest();
            return new GeneralResponse<>("200", "Succeed to query rule group list by rule group name", ruleGroupService.getOptionList(request));
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        }
    }

    @POST
    @Path("/rules/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse addRules(AddGroupRulesRequest request) throws UnExpectedRequestException, InterruptedException {
        try {
            request.checkRequest();
            List<AddGroupRuleRequest> addGroupRuleRequestList = request.getAddGroupRuleRequestList();
            if (addGroupRuleRequestList.size() > ONE_HANDARD) {
                throw new UnExpectedRequestException("Number of rules exceeded.");
            }
            // Check ruleGroup existence
            RuleGroup ruleGroupInDb = ruleGroupDao.findById(request.getRuleGroupId());
            if (ruleGroupInDb == null) {
                throw new UnExpectedRequestException(String.format("Rule Group: %s {&DOES_NOT_EXIST}", request.getRuleGroupId()));
            }
            RuleGroupResponse ruleGroupResponse = ruleGroupService.addRules(request, ruleGroupInDb);
            return new GeneralResponse<>(ruleGroupResponse.getCode(), ruleGroupResponse.getMessage(), ruleGroupResponse);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted!", e);
            Thread.currentThread().interrupt();
            throw new InterruptedException(e.getMessage());
        } catch (ExecutionException e) {
            throw new UnExpectedRequestException(e.getMessage());
        }
    }

    @POST
    @Path("/rules/modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse modifyRules(ModifyGroupRulesRequest request) throws UnExpectedRequestException, InterruptedException {
        try {
            request.checkRequest();
            List<ModifyGroupRuleRequest> modifyGroupRuleRequestList = request.getModifyGroupRuleRequestList();
            if (modifyGroupRuleRequestList.size() > ONE_HANDARD) {
                throw new UnExpectedRequestException("Number of rules exceeded.");
            }
            // Check ruleGroup existence
            RuleGroup ruleGroupInDb = ruleGroupDao.findById(request.getRuleGroupId());
            if (ruleGroupInDb == null) {
                throw new UnExpectedRequestException(String.format("Rule Group: %s {&DOES_NOT_EXIST}", request.getRuleGroupId()));
            }
            int ruleTotal = ruleDao.countByRuleGroup(ruleGroupInDb);
            RuleGroupResponse ruleGroupResponse = ruleGroupService.modifyRulesWithLock(request, ruleGroupInDb, ruleTotal);
            return new GeneralResponse<>(ruleGroupResponse.getCode(), ruleGroupResponse.getMessage(), ruleGroupResponse);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted!", e);
            Thread.currentThread().interrupt();
            throw new InterruptedException(e.getMessage());
        } catch (ExecutionException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (RuleLockException e) {
            throw new UnExpectedRequestException(e.getMessage());
        }
    }

    @POST
    @Path("/rules/query")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<RuleDetailResponse>> queryRules(QueryGroupRulesRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            request.checkRequest();
            // Check ruleGroup existence
            RuleGroup ruleGroupInDb = ruleGroupDao.findById(request.getRuleGroupId());
            if (ruleGroupInDb == null) {
                throw new UnExpectedRequestException(String.format("Rule Group: %s {&DOES_NOT_EXIST}", request.getRuleGroupId()));
            }
            return new GeneralResponse<>("200", "", ruleGroupService.queryRules(request, ruleGroupInDb));
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (PermissionDeniedRequestException e) {
            throw new PermissionDeniedRequestException(e.getMessage());
        }
    }

}
