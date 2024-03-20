package com.webank.wedatasphere.qualitis.timer;

import com.webank.wedatasphere.qualitis.constant.InvokeTypeEnum;
import com.webank.wedatasphere.qualitis.request.ProjectExecutionRequest;
import com.webank.wedatasphere.qualitis.request.RuleListExecutionRequest;
import com.webank.wedatasphere.qualitis.response.ApplicationProjectResponse;
import com.webank.wedatasphere.qualitis.service.ExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * @author v_gaojiedeng@webank.com
 */
public class RuleKindSumbitCallable implements Callable<ApplicationProjectResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleKindSumbitCallable.class);

    private ExecutionService executionService;

    private ProjectExecutionRequest projectExecutionRequest;

    private Integer code;

    private RuleListExecutionRequest ruleListExecutionRequest;

    private String loginUser;

    public RuleKindSumbitCallable(ProjectExecutionRequest projectExecutionRequest, RuleListExecutionRequest ruleListExecutionRequest, Integer code, ExecutionService executionService, String loginUser) {
        this.projectExecutionRequest = projectExecutionRequest;
        this.ruleListExecutionRequest = ruleListExecutionRequest;
        this.code = code;
        this.executionService = executionService;
        this.loginUser = loginUser;
    }

    @Override
    public ApplicationProjectResponse call() {
        ApplicationProjectResponse applicationProjectResponse = new ApplicationProjectResponse();
        try {
            LOGGER.info(Thread.currentThread().getName() + " start to sumbit rule task.");
            if (this.ruleListExecutionRequest != null) {
                try {
                    applicationProjectResponse = (ApplicationProjectResponse) executionService.ruleListExecution(this.ruleListExecutionRequest, InvokeTypeEnum.UI_INVOKE.getCode(), loginUser).getData();
                } catch (Exception e) {
                    LOGGER.error("Failed to execute rule list", e.getMessage(), e);
                    applicationProjectResponse.setExceptionMessage(e.getMessage());
                }
            } else if (this.projectExecutionRequest != null) {
                try {
                    applicationProjectResponse = (ApplicationProjectResponse) executionService.projectExecution(this.projectExecutionRequest, InvokeTypeEnum.UI_INVOKE.getCode(), loginUser).getData();
                } catch (Exception e) {
                    LOGGER.error("Failed to execute rule list", e.getMessage(), e);
                    applicationProjectResponse.setExceptionMessage(e.getMessage());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to execute rule list, caused by: {}", e.getMessage(), e);
        }
        return applicationProjectResponse;
    }
}
