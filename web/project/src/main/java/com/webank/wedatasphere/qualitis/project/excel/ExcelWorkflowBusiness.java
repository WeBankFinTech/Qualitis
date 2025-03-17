package com.webank.wedatasphere.qualitis.project.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflowBusiness;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;

/**
 * @author v_minminghe@webank.com
 * @date 2024-07-17 14:57
 * @description
 */
public class ExcelWorkflowBusiness {

    @ExcelProperty(value = "scheduled workflow business", index = 0)
    private String scheduledWorkflowBusinessJsonObject;

    public static ExcelWorkflowBusiness from(ScheduledWorkflowBusiness scheduledWorkflowBusiness) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ExcelWorkflowBusiness excelWorkflowBusiness = new ExcelWorkflowBusiness();

        excelWorkflowBusiness.setScheduledWorkflowBusinessJsonObject(objectMapper.writeValueAsString(scheduledWorkflowBusiness));

        return excelWorkflowBusiness;
    }

    public ScheduledWorkflowBusiness getScheduledWorkflowBusinesses(ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(scheduledWorkflowBusinessJsonObject, new TypeReference<ScheduledWorkflowBusiness>() {
            });
        } catch (IOException e) {
            //            Don't to do anything
        }
        return null;
    }

    public String getScheduledWorkflowBusinessJsonObject() {
        return scheduledWorkflowBusinessJsonObject;
    }

    public void setScheduledWorkflowBusinessJsonObject(String scheduledWorkflowBusinessJsonObject) {
        this.scheduledWorkflowBusinessJsonObject = scheduledWorkflowBusinessJsonObject;
    }
}
