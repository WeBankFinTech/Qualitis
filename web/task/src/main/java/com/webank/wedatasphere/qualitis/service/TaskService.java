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

package com.webank.wedatasphere.qualitis.service;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.TaskCheckResultResponse;

/**
 * @author howeye
 */
public interface TaskService {

    /**
     * Find task detail by taskId
     * @param taskId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<TaskCheckResultResponse> getTaskDetail(Long taskId) throws UnExpectedRequestException;

    /**
     * To get number of task executing
     * @param dataRangeInHours
     * @return
     */
    Long getExecutingTaskNumber(int dataRangeInHours);

}
