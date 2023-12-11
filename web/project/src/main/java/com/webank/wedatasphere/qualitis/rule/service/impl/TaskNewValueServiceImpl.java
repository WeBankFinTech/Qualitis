package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.webank.wedatasphere.qualitis.constant.NewValueStatusEnum;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.query.request.TaskNewValueRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.TaskNewVauleDao;
import com.webank.wedatasphere.qualitis.rule.entity.TaskNewValue;
import com.webank.wedatasphere.qualitis.rule.request.ModifyTaskNewValueRequest;
import com.webank.wedatasphere.qualitis.rule.response.TaskNewValueResponse;
import com.webank.wedatasphere.qualitis.rule.service.TaskNewValueService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
@Service
public class TaskNewValueServiceImpl implements TaskNewValueService {

    @Autowired
    private TaskNewVauleDao taskNewValueDao;
    @Autowired
    private RuleDao ruleDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskNewValueServiceImpl.class);

    public static final FastDateFormat PRINT_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private HttpServletRequest httpServletRequest;

    public TaskNewValueServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public TaskNewValueResponse modifyTaskNewValue(ModifyTaskNewValueRequest request) throws UnExpectedRequestException {
        ModifyTaskNewValueRequest.checkRequest(request);

        TaskNewValue taskNewValue = taskNewValueDao.findById(request.getTaskNewValueId());
        if (taskNewValue == null) {
            throw new UnExpectedRequestException("task_new_value_id :[" + request.getTaskNewValueId() + "] {&DOES_NOT_EXIST}");
        }

        //查询匹配数据
        List<TaskNewValue> list = taskNewValueDao.selectExactTaskNewValue(taskNewValue.getRuleId(), taskNewValue.getStatus(), taskNewValue.getResultValue());

        if (CollectionUtils.isNotEmpty(list)) {
            for (TaskNewValue newValue : list) {
                handleTaskNewValue(request, newValue);
            }
        } else {
            handleTaskNewValue(request, taskNewValue);
        }
        TaskNewValueResponse response = new TaskNewValueResponse();
        LOGGER.info("Succeed to modify TaskNewValue");
        return response;
    }

    private void handleTaskNewValue(ModifyTaskNewValueRequest request, TaskNewValue taskNewValue) {
        BeanUtils.copyProperties(request, taskNewValue);
        //枚举的字符或者数值，字符串也有可能
        Set<Object> set = new HashSet<>();
        set.add(request.getResultValue());
        taskNewValue.setResultValue(set.iterator().next().toString());
        //状态值set （1未处理2已录入3已丢弃）
        if (NewValueStatusEnum.ENTERED.getCode().equals(request.getType())) {
            taskNewValue.setStatus(Long.parseLong(String.valueOf(2)));
        } else if (NewValueStatusEnum.DISCARDED.equals(request.getType())) {
            taskNewValue.setStatus(Long.parseLong(String.valueOf(3)));
        }

        taskNewValue.setModifyUser(HttpUtils.getUserName(httpServletRequest));
        taskNewValue.setModifyTime(TaskNewValueServiceImpl.PRINT_TIME_FORMAT.format(new Date()));
        taskNewValueDao.saveTaskNewValue(taskNewValue);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public void deleteTaskNewValue(Long id) throws UnExpectedRequestException {
        TaskNewValue taskNewValue = checkTaskNewValue(id);
        //查询匹配数据
        List<TaskNewValue> list = taskNewValueDao.selectExactTaskNewValue(taskNewValue.getRuleId(), taskNewValue.getStatus(), taskNewValue.getResultValue());
        if (CollectionUtils.isNotEmpty(list)) {
            for (TaskNewValue newValue : list) {
                newValue.setStatus(Long.parseLong(String.valueOf(3)));
                newValue.setModifyUser(HttpUtils.getUserName(httpServletRequest));
                newValue.setModifyTime(TaskNewValueServiceImpl.PRINT_TIME_FORMAT.format(new Date()));
                taskNewValueDao.saveTaskNewValue(newValue);
            }
        } else {
            taskNewValue.setStatus(Long.parseLong(String.valueOf(3)));
            taskNewValue.setModifyUser(HttpUtils.getUserName(httpServletRequest));
            taskNewValue.setModifyTime(TaskNewValueServiceImpl.PRINT_TIME_FORMAT.format(new Date()));
            taskNewValueDao.saveTaskNewValue(taskNewValue);
        }
    }

    public TaskNewValue checkTaskNewValue(Long id) throws UnExpectedRequestException {
        TaskNewValue taskNewValue = taskNewValueDao.findById(id);
        if (taskNewValue == null) {
            throw new UnExpectedRequestException("TaskNewValue {&DOES_NOT_EXIST}");
        }
        return taskNewValue;
    }

    @Override
    public GetAllResponse<TaskNewValueResponse> getAllTaskNewValue(TaskNewValueRequest request) throws UnExpectedRequestException {
        TaskNewValueRequest.checkRequest(request);

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(request.getPage());
        pageRequest.setSize(request.getSize());
        // Check Arguments
        PageRequest.checkRequest(pageRequest);
        // Paging get TaskNewValue
        int page = request.getPage();
        int size = request.getSize();
        GetAllResponse<TaskNewValueResponse> response = new GetAllResponse<>();
        List<TaskNewValue> taskNewValueList = taskNewValueDao.findAllTaskNewValue(request.getRuleId(), page, size);
        Integer total = taskNewValueDao.countAllStandardValue(request.getRuleId());
        List<TaskNewValueResponse> taskNewValueResponse = new ArrayList<>();
        for (TaskNewValue taskNewValue : taskNewValueList) {
            taskNewValueResponse.add(new TaskNewValueResponse(taskNewValue));
        }
        response.setData(taskNewValueResponse);
        response.setTotal(total);
        return response;
    }

    @Override
    public TaskNewValueResponse getTaskNewValueIdDetail(Long taskNewValueId) throws UnExpectedRequestException {
        TaskNewValue taskNewValue = checkTaskNewValue(taskNewValueId);

        TaskNewValueResponse response = new TaskNewValueResponse(taskNewValue);
        LOGGER.info("Succeed to get TaskNewValue detail. task_new_value_id: {}", taskNewValueId);
        return response;
    }


}
