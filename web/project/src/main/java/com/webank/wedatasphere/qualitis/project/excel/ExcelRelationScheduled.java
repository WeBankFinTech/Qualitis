package com.webank.wedatasphere.qualitis.project.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.webank.wedatasphere.qualitis.project.response.ScheduledFrontBackRuleResponse;
import com.webank.wedatasphere.qualitis.project.response.ScheduledTaskResponse;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledFrontBackRule;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledTask;
import org.apache.commons.collections4.CollectionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author v_minminghe@webank.com
 * @date 2023-04-19 15:26
 * @description
 */
public class ExcelRelationScheduled extends BaseRowModel {

    @ExcelProperty(value = "SCHEDULED TASK", index = 0)
    private String scheduledTaskJsonObject;

    @ExcelProperty(value = "SCHEDULED FRONT BACK RULES", index = 1)
    private String scheduledFrontBackRuleJsonObject;

    public static ExcelRelationScheduled fromScheduledTask(ScheduledTask scheduledTask, List<ScheduledFrontBackRule> scheduledFrontBackRuleList) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ExcelRelationScheduled excelRelationScheduled = new ExcelRelationScheduled();
        excelRelationScheduled.setScheduledTaskJsonObject(objectMapper.writeValueAsString(new ScheduledTaskResponse(scheduledTask)));
        if (CollectionUtils.isNotEmpty(scheduledFrontBackRuleList)) {
            List<ScheduledFrontBackRuleResponse> backRuleList = scheduledFrontBackRuleList.stream().map(ScheduledFrontBackRuleResponse::new).collect(Collectors.toList());
            excelRelationScheduled.setScheduledFrontBackRuleJsonObject(objectMapper.writeValueAsString(backRuleList));
        }
        return excelRelationScheduled;
    }

    public String getScheduledTaskJsonObject() {
        return scheduledTaskJsonObject;
    }

    public void setScheduledTaskJsonObject(String scheduledTaskJsonObject) {
        this.scheduledTaskJsonObject = scheduledTaskJsonObject;
    }

    public String getScheduledFrontBackRuleJsonObject() {
        return scheduledFrontBackRuleJsonObject;
    }

    public void setScheduledFrontBackRuleJsonObject(String scheduledFrontBackRuleJsonObject) {
        this.scheduledFrontBackRuleJsonObject = scheduledFrontBackRuleJsonObject;
    }

    public ScheduledTaskResponse getScheduledTask(ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(this.scheduledTaskJsonObject, ScheduledTaskResponse.class);
        } catch (IOException e) {
//            Don't to do anything
        }
        return null;
    }

    public List<ScheduledFrontBackRuleResponse> getScheduledFrontBackRule(ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(this.scheduledFrontBackRuleJsonObject, new TypeReference<List<ScheduledFrontBackRuleResponse>>() {
            });
        } catch (IOException e) {
//            Don't to do anything
        }
        return Collections.emptyList();
    }
}
