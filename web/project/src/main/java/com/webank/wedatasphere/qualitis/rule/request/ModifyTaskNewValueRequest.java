package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

/**
 * @author v_gaojiedeng@webank.com
 */
public class ModifyTaskNewValueRequest {
    @JsonProperty("task_new_value_id")
    private Long taskNewValueId;
    @JsonProperty("result_value")
    private Object resultValue;
    @JsonProperty("type")
    private Integer type;

    public Long getTaskNewValueId() {
        return taskNewValueId;
    }

    public void setTaskNewValueId(Long taskNewValueId) {
        this.taskNewValueId = taskNewValueId;
    }

    public Object getResultValue() {
        return resultValue;
    }

    public void setResultValue(Object resultValue) {
        this.resultValue = resultValue;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public static void checkRequest(ModifyTaskNewValueRequest request)
            throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkObject(request.getTaskNewValueId(), "task_new_value_id");
        CommonChecker.checkObject(request.getResultValue(), "result_value");
        CommonChecker.checkObject(request.getType(), "type");
    }

}
