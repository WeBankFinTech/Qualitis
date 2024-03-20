package com.webank.wedatasphere.qualitis.timer;

import com.webank.wedatasphere.qualitis.constant.InvokeTypeEnum;
import com.webank.wedatasphere.qualitis.request.GroupListExecutionRequest;
import com.webank.wedatasphere.qualitis.response.ApplicationTaskSimpleResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.ExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * @author v_gaojiedeng@webank.com
 */
public class RuleGroupSumbitCallable implements Callable<ApplicationTaskSimpleResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleGroupSumbitCallable.class);

    private ExecutionService executionService;

    private GroupListExecutionRequest groupListExecutionRequest;

    private Integer code;

    private String loginUser;

    public RuleGroupSumbitCallable(GroupListExecutionRequest groupListExecutionRequest, Integer code, ExecutionService executionService, String loginUser) {
        this.groupListExecutionRequest = groupListExecutionRequest;
        this.code = code;
        this.executionService = executionService;
        this.loginUser = loginUser;
    }

    @Override
    public ApplicationTaskSimpleResponse call() {
        ApplicationTaskSimpleResponse applicationTaskSimpleResponse = new ApplicationTaskSimpleResponse();
        try {
            LOGGER.info(Thread.currentThread().getName() + " start to sumbit rule task.");
            try {
                GeneralResponse generalResponse = executionService.ruleGroupListExecution(this.groupListExecutionRequest, InvokeTypeEnum.UI_INVOKE.getCode(), loginUser);
                applicationTaskSimpleResponse.setCode(generalResponse.getCode());
            } catch (Exception e) {
                LOGGER.error("Failed to execute rule list", e.getMessage(), e);
                applicationTaskSimpleResponse.setExceptionMessage(e.getMessage());
            }

        } catch (Exception e) {
            LOGGER.error("Failed to execute rule list, caused by: {}", e.getMessage(), e);
        }
        return applicationTaskSimpleResponse;
    }
}
