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
import com.webank.wedatasphere.qualitis.rule.response.TemplateMetaResponse;
import com.webank.wedatasphere.qualitis.rule.service.RuleTemplateService;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.service.RuleTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author v_wblwyan
 * @date 2019-5-17
 */
@Path("api/v1/projector/multi_rule_template")
public class MultiRuleTemplateController {

    @Autowired
    private RuleTemplateService ruleTemplateService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiRuleTemplateController.class);

    /**
     * Get meta data of multi-table rule by id
     * @param ruleTemplateId
     * @return
     * @throws UnExpectedRequestException
     */
    @GET
    @Path("meta/{rule_template_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<TemplateMetaResponse> getRuleTemplateMeta(@PathParam("rule_template_id") Long ruleTemplateId) throws
        UnExpectedRequestException {
        try {
            return ruleTemplateService.getRuleMultiTemplateMeta(ruleTemplateId);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get multi rule_template. multi_rule_template_id: {}, caused by: {}", ruleTemplateId, e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_RULE_TEMPLATE}", null);
        }
    }

}
