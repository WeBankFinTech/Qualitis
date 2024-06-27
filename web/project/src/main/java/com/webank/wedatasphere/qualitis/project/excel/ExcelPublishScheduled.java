package com.webank.wedatasphere.qualitis.project.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.webank.wedatasphere.qualitis.project.response.*;
import com.webank.wedatasphere.qualitis.scheduled.entity.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author v_minminghe@webank.com
 * @date 2023-04-19 14:46
 * @description
 */
public class ExcelPublishScheduled extends BaseRowModel {

    @ExcelProperty(value = "SCHEDULED PROJECT", index = 0)
    private String scheduledProjectJsonObject;

    @ExcelProperty(value = "SCHEDULED WORKFLOW", index = 1)
    private String scheduledWorkflowJsonObject;

    @ExcelProperty(value = "SCHEDULED SIGNAL", index = 2)
    private String scheduledSignalJsonObject;

    @ExcelProperty(value = "SCHEDULED TASK", index = 3)
    private String scheduledTaskJsonObject;

    @ExcelProperty(value = "SCHEDULED WORKFLOW AND TASK RELATION", index = 4)
    private String scheduledWorkflowAndTaskRelationJsonObject;

    public static ExcelPublishScheduled fromScheduledProject(ScheduledProject scheduledProject, List<ScheduledWorkflow> scheduledWorkflowList
            , List<ScheduledSignal> scheduledSignalList, List<ScheduledTask> scheduledTaskList, List<ScheduledWorkflowTaskRelation> scheduledWorkflowTaskRelationList) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ExcelPublishScheduled rowPublishScheduled = new ExcelPublishScheduled();

        rowPublishScheduled.setScheduledProjectJsonObject(objectMapper.writeValueAsString(new ScheduledProjectResponse(scheduledProject)));
        List<ScheduledWorkflowResponse> cellScheduledWorkflows = scheduledWorkflowList.stream().map(ScheduledWorkflowResponse::new).collect(Collectors.toList());
        rowPublishScheduled.setScheduledWorkflowJsonObject(objectMapper.writeValueAsString(cellScheduledWorkflows));

        List<ScheduledSignalResponse> cellScheduledSignals = scheduledSignalList.stream().map(ScheduledSignalResponse::new).collect(Collectors.toList());
        rowPublishScheduled.setScheduledSignalJsonObject(objectMapper.writeValueAsString(cellScheduledSignals));

        List<ScheduledTaskResponse> cellScheduledTasks = scheduledTaskList.stream().map(ScheduledTaskResponse::new).collect(Collectors.toList());
        rowPublishScheduled.setScheduledTaskJsonObject(objectMapper.writeValueAsString(cellScheduledTasks));

        List<ScheduledWorkflowTaskRelationResponse> cellScheduledWorkflowTaskRelations = scheduledWorkflowTaskRelationList.stream().map(ScheduledWorkflowTaskRelationResponse::new).collect(Collectors.toList());
        rowPublishScheduled.setScheduledWorkflowAndTaskRelationJsonObject(objectMapper.writeValueAsString(cellScheduledWorkflowTaskRelations));

        return rowPublishScheduled;
    }

    public String getScheduledProjectJsonObject() {
        return scheduledProjectJsonObject;
    }

    public void setScheduledProjectJsonObject(String scheduledProjectJsonObject) {
        this.scheduledProjectJsonObject = scheduledProjectJsonObject;
    }

    public String getScheduledWorkflowJsonObject() {
        return scheduledWorkflowJsonObject;
    }

    public void setScheduledWorkflowJsonObject(String scheduledWorkflowJsonObject) {
        this.scheduledWorkflowJsonObject = scheduledWorkflowJsonObject;
    }

    public String getScheduledSignalJsonObject() {
        return scheduledSignalJsonObject;
    }

    public void setScheduledSignalJsonObject(String scheduledSignalJsonObject) {
        this.scheduledSignalJsonObject = scheduledSignalJsonObject;
    }

    public String getScheduledTaskJsonObject() {
        return scheduledTaskJsonObject;
    }

    public void setScheduledTaskJsonObject(String scheduledTaskJsonObject) {
        this.scheduledTaskJsonObject = scheduledTaskJsonObject;
    }

    public String getScheduledWorkflowAndTaskRelationJsonObject() {
        return scheduledWorkflowAndTaskRelationJsonObject;
    }

    public void setScheduledWorkflowAndTaskRelationJsonObject(String scheduledWorkflowAndTaskRelationJsonObject) {
        this.scheduledWorkflowAndTaskRelationJsonObject = scheduledWorkflowAndTaskRelationJsonObject;
    }

    public ScheduledProjectResponse getScheduledProject(ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(scheduledProjectJsonObject, ScheduledProjectResponse.class);
        } catch (IOException e) {
//            Don't to do anything
        }
        return null;
    }

    public List<ScheduledWorkflowResponse> getScheduledWorkflows(ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(scheduledWorkflowJsonObject, new TypeReference<List<ScheduledWorkflowResponse>>() {
            });
        } catch (IOException e) {
//            Don't to do anything
        }
        return Collections.emptyList();
    }

    public List<ScheduledSignalResponse> getScheduledSignals(ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(scheduledSignalJsonObject, new TypeReference<List<ScheduledSignalResponse>>() {
            });
        } catch (IOException e) {
            //            Don't to do anything
        }
        return Collections.emptyList();
    }

    public List<ScheduledTaskResponse> getScheduledTasks(ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(scheduledTaskJsonObject, new TypeReference<List<ScheduledTaskResponse>>() {
            });
        } catch (IOException e) {
            //            Don't to do anything
        }
        return Collections.emptyList();
    }

    public List<ScheduledWorkflowTaskRelationResponse> getScheduledWorkflowTaskRelations(ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(scheduledWorkflowAndTaskRelationJsonObject, new TypeReference<List<ScheduledWorkflowTaskRelationResponse>>() {
            });
        } catch (IOException e) {
            //            Don't to do anything
        }
        return Collections.emptyList();
    }
}
