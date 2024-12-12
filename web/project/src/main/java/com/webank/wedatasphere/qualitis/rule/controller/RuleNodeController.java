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
import com.webank.wedatasphere.qualitis.rule.request.BatchExecutionParametersRequest;
import com.webank.wedatasphere.qualitis.rule.request.CopyRuleWithDatasourceRequest;
import com.webank.wedatasphere.qualitis.rule.response.CopyRuleWithDatasourceResponse;
import com.webank.wedatasphere.qualitis.rule.service.RuleNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author allenzhou
 */
@Path("api/v1/projector/rule")
public class RuleNodeController {
    @Autowired
    private RuleNodeService ruleNodeService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleNodeController.class);

    @POST
    @Path("copy")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse copyRuleWithDatasource(CopyRuleWithDatasourceRequest request) throws UnExpectedRequestException {
        try {
            return ruleNodeService.copyRuleWithDatasource(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to copy rule. project id: {}, caused by: {}", request.getProjectId(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_COPY_RULE}", null);
        }
    }

    @POST
    @Path("batchUpdate/ruleParameters")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<Long>> batchUpdateParameters(BatchExecutionParametersRequest request) throws UnExpectedRequestException {
        try {
            return ruleNodeService.updateRuleDataForBatch(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to batch update rule. caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_BATCH_MODIFY_RULE}", null);
        }
    }

}
