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

import com.webank.wedatasphere.qualitis.pool.exception.ThreadPoolNotFoundException;
import com.webank.wedatasphere.qualitis.pool.manager.AbstractThreadPoolManager;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.constants.ThreadPoolConstant;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.project.response.ProjectDetailResponse;
import com.webank.wedatasphere.qualitis.project.service.ProjectBatchService;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.request.*;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.request.DeleteRuleMetricRequest;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import com.webank.wedatasphere.qualitis.service.OuterExecutionService;
import com.webank.wedatasphere.qualitis.service.RuleMetricCommonService;
import com.webank.wedatasphere.qualitis.timer.RuleLisingCallable;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author howeye
 */
@Path("outer/api/v1")
public class OuterExecutionController {
    @Autowired
    private OuterExecutionService outerExecutionService;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectBatchService projectBatchService;
    @Autowired
    private RuleMetricCommonService ruleMetricCommonService;
    @Autowired
    private AbstractThreadPoolManager poolManager;
    private ThreadPoolExecutor outerRuleExecutionPool;

    private static final Logger LOGGER = LoggerFactory.getLogger(OuterExecutionController.class);

    private HttpServletRequest httpServletRequest;

    public OuterExecutionController(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @PostConstruct
    public void init() throws ThreadPoolNotFoundException {
        this.outerRuleExecutionPool = poolManager.getThreadPool(ThreadPoolConstant.OUTER_RULE_EXECUTION);
    }

    @POST
    @Path("/rule/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleResponse> addRule(GeneralAddRuleRequest request) {
        try {
            String loginUser = request.getCreateUser();
            return outerExecutionService.addRule(request, loginUser);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "failed_add_rule" + e.getMessage(), null);
        }
    }

    @POST
    @Path("execution")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> generalExecution(GeneralExecutionRequest request) {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        try {
            if (request.getAsync()) {
                LOGGER.info("Start to async run submit application.");
                outerRuleExecutionPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            outerExecutionService.generalExecution(request,loginUser);
                        } catch (UnExpectedRequestException e) {
                            LOGGER.error(e.getMessage(), e);
                        } catch (Exception e) {
                            LOGGER.error("Async failed exception.", e);
                        }
                    }
                });

                return new GeneralResponse<>(ResponseStatusConstants.OK, "{&SUCCESS_ASYNC_SUBMIT_TASK}", null);
            } else {
                return outerExecutionService.generalExecution(request,loginUser);
            }
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), e);
        }  catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Failed to dispatch application, caused by: {}. Exception: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_DISPATCH_APPLICATION}", e);
        }
    }

    @GET
    @Path("execution/application/kill/{applicationId}/{executionUser}")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> killApplication(@PathParam("applicationId") String applicationId, @PathParam("executionUser") String executionUser) {
        try {
            return outerExecutionService.killApplication(applicationId, executionUser);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Failed to kill application: {}", applicationId, e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_KILL_TASK}: " + applicationId, e);
        }
    }

    @GET
    @Path("application/{applicationId}/status")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> getApplicationStatus(@PathParam("applicationId") String applicationId) {
        try {
            return outerExecutionService.getApplicationStatus(applicationId);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Failed to get application status. application_id: {}, caused by: {}", applicationId, e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_APPLICATION_STATUS}， caused by: " + e.getMessage(), e);
        }
    }

    @GET
    @Path("application/dynamic/{applicationId}/status")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> getApplicationDynamicStatus(@PathParam("applicationId") String applicationId) {
        try {
            return outerExecutionService.getApplicationDynamicStatus(applicationId);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Failed to get application status. application_id: {}, caused by: {}", applicationId, e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_APPLICATION_STATUS}， caused by: " + e.getMessage(), e);
        }
    }

    @GET
    @Path("application/{applicationId}/result")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> getApplicationSummary(@PathParam("applicationId") String applicationId) {
        try {
            return outerExecutionService.getApplicationSummary(applicationId);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Failed to get result of application: {}", applicationId, e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_RESULT_OF_APPLICATION}: " + applicationId, e);
        }
    }

    @GET
    @Path("application/{applicationId}/{executionUser}/result_value")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> getApplicationResultValue(@PathParam("applicationId") String applicationId, @PathParam("executionUser") String executionUser) {
        try {
            return outerExecutionService.getApplicationResultValue(applicationId, executionUser);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Failed to get result of application: {}", applicationId, e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_RESULT_OF_APPLICATION}: " + applicationId, e);
        }
    }

    @POST
    @Path("log")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> getTaskLog(GetTaskLogRequest request) {
        try {
            return outerExecutionService.getTaskLog(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Failed to get log of the task: {}, cluster_id: {}", request.getTaskId(), request.getClusterId(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_LOG_OF_THE_TASK}", e);
        }
    }

    @GET
    @Path("application/{applicationId}/log")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> getTaskLog(@PathParam("applicationId") String applicationId) {
        try {
            return outerExecutionService.getApplicationLog(applicationId);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Failed to get log of the application: ID={}", applicationId, e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_LOG_OF_THE_TASK}", e);
        }
    }

    @POST
    @Path("query")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> queryJobInfo(ApplicationQueryRequest request) {
        try {
            return outerExecutionService.queryJobInfo(request.getJobId());
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Failed to query application by job ID: {}", request.getJobId(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_QUERY_TASK}", e);
        }
    }

    @POST
    @Path("query/application")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> queryApplications(ApplicationQueryRequest request) {
        try {
            return outerExecutionService.queryApplications(request.getJobId());
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Failed to query application by job ID: {}", request.getJobId(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_QUERY_TASK}: " + request.getJobId(), e);
        }
    }

    @POST
    @Path("query/rule")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> queryRule(GetRuleQueryRequest request) {
        try {
            Future<ProjectDetailResponse> projectDetailResponse = outerRuleExecutionPool.submit(new RuleLisingCallable(request, ruleDao, projectDao, projectService));

            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&SUCCESS_GET_RULE_QUERY}", projectDetailResponse.get());
        } catch (Exception e) {
            LOGGER.error("Failed to get rule query. caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_RULE_QUERY}", e.getMessage());
        }
    }

    @POST
    @Path("batch/delete/rule")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> deleteRule(BatchDeleteRuleRequest request) {
        try {
            outerExecutionService.deleteRule(request);
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&SUCCESS_DELETE_RULE_LIST}", null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        }  catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error("Failed to delete rule . caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_DELETE_RULE_LIST}", e.getMessage());
        }

    }

//    @POST
//    @Path("query/identify")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse<Object> queryIdentifyConfig(OmnisScriptRequest request) {
//        try {
//            return outerExecutionService.queryIdentify(request);
//        } catch (Exception e) {
//            LOGGER.error("queryIdentifyConfig Failed  caused by system error: {}", e.getMessage(), e);
//            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&CAN_NOT_FIND_SUITABLE_REQUEST}", e.getMessage());
//        }
//    }

//    @POST
//    @Path("query/ImsmetricData")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse<Object> queryImsmetricData(OmnisScriptRequest request) {
//        try {
//            return outerExecutionService.queryImsmetricData(request);
//        } catch (Exception e) {
//            LOGGER.error("queryIdentifyConfig Failed  caused by system error: {}", e.getMessage(), e);
//            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&CAN_NOT_FIND_SUITABLE_REQUEST}", e.getMessage());
//        }
//    }

    @POST
    @Path("batch/enable/rule")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> batchEnableRule(EnableOrDisableRule request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            return outerExecutionService.batchEnableOrDisableRule(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to batch enable or disable rule . caused by system error: {}. Exception: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_BATCH_ENABLE_OR_DISABLE_RULE}", e);
        }
    }

    @POST
    @Path("application/result")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> getApplicationResult(ApplicationIdListQueryRequest request) {
        try {
            return outerExecutionService.getApplicationResult(request.getApplicationIdList());
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Failed to get result of application: {}", e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_RESULT_OF_APPLICATION}:", e);
        }
    }

//    @POST
//    @Path("fieldsAnalyse/result")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public GeneralResponse<Object> getFieldsAnalyseResult(FieldsAnalyseRequest request) {
//        try {
//            return outerExecutionService.getFieldsAnalyseResult(request);
//        } catch (UnExpectedRequestException e) {
//            LOGGER.error(e.getMessage(), e);
//            return new GeneralResponse<>("500", e.getMessage(), e);
//        } catch (Exception e) {
//            LOGGER.error("Failed to get result of application: {}", e);
//            return new GeneralResponse<>("500", "{&FAILED_TO_GET_RESULT_OF_APPLICATION}:", e);
//        }
//    }

    @POST
    @Path("delete/rule_metrics")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> deleteRuleMetrics(DeleteRuleMetricRequest deleteRuleMetricRequest) {
        try {
            CommonChecker.checkString(deleteRuleMetricRequest.getName(), "name");
            CommonChecker.checkString(deleteRuleMetricRequest.getUsername(), "username");
            ruleMetricCommonService.deleteRuleMetric(deleteRuleMetricRequest.getName(), deleteRuleMetricRequest.getUsername());
        } catch (Exception e) {
            LOGGER.error("Failed to delete rule metrics, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_DELETE_RULE_METRIC}", null);
        }
        return new GeneralResponse<>(ResponseStatusConstants.OK, "success", null);
    }

}
