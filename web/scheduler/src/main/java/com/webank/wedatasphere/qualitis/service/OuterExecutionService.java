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

import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.request.GeneralExecutionRequest;
import com.webank.wedatasphere.qualitis.exception.*;
import com.webank.wedatasphere.qualitis.request.GetTaskLogRequest;
import com.webank.wedatasphere.qualitis.request.ProjectExecutionRequest;
import com.webank.wedatasphere.qualitis.request.RuleListExecutionRequest;
import com.webank.wedatasphere.qualitis.response.ApplicationTaskSimpleResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.exception.*;
import com.webank.wedatasphere.qualitis.request.GeneralExecutionRequest;
import com.webank.wedatasphere.qualitis.request.GetTaskLogRequest;
import com.webank.wedatasphere.qualitis.request.ProjectExecutionRequest;
import com.webank.wedatasphere.qualitis.request.RuleListExecutionRequest;
import com.webank.wedatasphere.qualitis.response.ApplicationTaskSimpleResponse;
import com.webank.wedatasphere.qualitis.request.GeneralExecutionRequest;
import com.webank.wedatasphere.qualitis.request.GetTaskLogRequest;
import com.webank.wedatasphere.qualitis.request.ProjectExecutionRequest;
import com.webank.wedatasphere.qualitis.request.RuleListExecutionRequest;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author howeye
 */
public interface OuterExecutionService {

    /**
     * 通用执行接口
     * @param request 执行参数
     * @return 处理结果
     * @throws UnExpectedRequestException 参数时抛出
     */
    GeneralResponse<?> generalExecution(GeneralExecutionRequest request) throws UnExpectedRequestException;


    /**
     * 获取任务状态信息
     * @param applicationId 任务id
     * @return 任务信息
     * @throws UnExpectedRequestException 参数异常
     */
    GeneralResponse<?> getApplicationStatus(String applicationId) throws UnExpectedRequestException;

    /**
     * 获取任务日志
     * @param request 请求
     * @return 任务日志
     * @throws UnExpectedRequestException 参数异常
     */
    GeneralResponse<?> getTaskLog(GetTaskLogRequest request) throws UnExpectedRequestException;

    /**
     * 通用任务执行
     * @param rules
     * @param partition
     * @param executionUser
     * @param cluster
     * @param date
     * @param newApplication
     * @return
     * @throws RuleVariableNotSupportException
     * @throws JobSubmitException
     * @throws RuleVariableNotFoundException
     * @throws ArgumentException
     * @throws ConvertException
     * @throws DataQualityTaskException
     * @throws TaskTypeException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws ClusterInfoNotConfigException
     */
    ApplicationTaskSimpleResponse commonExecution(List<Rule> rules, String partition, String executionUser, Application newApplication, Date date)
        throws RuleVariableNotSupportException,
        JobSubmitException, RuleVariableNotFoundException, ArgumentException, ConvertException, DataQualityTaskException, TaskTypeException, ExecutionException, InterruptedException, ClusterInfoNotConfigException, SystemConfigException;

    /**
     * 生成applicationInfo
     * @param createUser
     * @param executionUser
     * @param date
     * @return
     */
    Application generateApplicationInfo(String createUser, String executionUser, Date date);

    /**
     * 项目调度
     * @param request
     * @param newApplication
     * @param date
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> projectExecution(ProjectExecutionRequest request, Application newApplication, Date date) throws UnExpectedRequestException;

    /**
     * 规则列表调度
     * @param request
     * @param newApplication
     * @param date
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> ruleListExecution(RuleListExecutionRequest request, Application newApplication, Date date) throws UnExpectedRequestException;

    /**
     * 获取application的结果状态
     * @param applicationId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> getApplicationResult(String applicationId) throws UnExpectedRequestException;
}
