package com.webank.wedatasphere.qualitis.rule.service;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.query.request.TaskNewValueRequest;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.request.ModifyTaskNewValueRequest;
import com.webank.wedatasphere.qualitis.rule.response.TaskNewValueResponse;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface TaskNewValueService {

    /**
     * modify taskNewValue
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    TaskNewValueResponse modifyTaskNewValue(ModifyTaskNewValueRequest request)
            throws UnExpectedRequestException;

    /**
     * delete taskNewValue
     *
     * @param id
     * @return
     * @throws UnExpectedRequestException
     */
    void deleteTaskNewValue(Long id) throws UnExpectedRequestException;

    /**
     * get All taskNewValue
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GetAllResponse<TaskNewValueResponse> getAllTaskNewValue(TaskNewValueRequest request) throws UnExpectedRequestException;

    /**
     * get taskNewValue Detail
     *
     * @param taskNewValueId
     * @return
     * @throws UnExpectedRequestException
     */
    TaskNewValueResponse getTaskNewValueIdDetail(Long taskNewValueId) throws UnExpectedRequestException;

}
