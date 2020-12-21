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

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.response.RuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleGroupResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Path("api/v1/projector/rule/group")
public class RuleGroupController {

    @Autowired
    private RuleGroupDao ruleGroupDao;

    @Autowired
    private RuleDao ruleDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleGroupController.class);

    @GET
    @Path("/{rule_group_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> getRuleIdByRule(@PathParam("rule_group_id")Long ruleGroupId) throws UnExpectedRequestException {
        try {
            // 查看ruleGroup是否存在
            RuleGroup ruleGroupInDb = ruleGroupDao.findById(ruleGroupId);
            if (ruleGroupInDb == null) {
                throw new UnExpectedRequestException(String.format("Rule Group: %s {&DOES_NOT_EXIST}", ruleGroupId));
            }

            List<RuleResponse> ruleList = ruleDao.findByRuleGroup(ruleGroupInDb).stream().map(rule -> new RuleResponse(rule)).collect(Collectors.toList());
            return new GeneralResponse<>("200", "Succeed to find rules by rule group id",
                    new RuleGroupResponse(ruleGroupId, ruleList));
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get rules by rule group id. rule_group_id: {}, caused by: {}", ruleGroupId.toString().replace("\r", "")
                .replace("\n", ""), e.getMessage().replace("\r", "").replace("\n", ""), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_RULES_BY_RULE_GROUP}", null);
        }
    }


}
