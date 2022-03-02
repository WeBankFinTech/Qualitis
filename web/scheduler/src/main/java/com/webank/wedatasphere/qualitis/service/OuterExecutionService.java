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
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.request.DataSourceExecutionRequest;
import com.webank.wedatasphere.qualitis.request.GeneralExecutionRequest;
import com.webank.wedatasphere.qualitis.exception.*;
import com.webank.wedatasphere.qualitis.request.GetTaskLogRequest;
import com.webank.wedatasphere.qualitis.request.GroupExecutionRequest;
import com.webank.wedatasphere.qualitis.request.ProjectExecutionRequest;
import com.webank.wedatasphere.qualitis.request.RuleListExecutionRequest;
import com.webank.wedatasphere.qualitis.response.ApplicationTaskSimpleResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.hive.ql.parse.SemanticException;

/**
 * @author howeye
 */
public interface OuterExecutionService {

    /**
     * 通用执行接口
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<?> generalExecution(GeneralExecutionRequest request)
        throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Datasource execution.
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<?> dataSourceExecution(DataSourceExecutionRequest request)
        throws UnExpectedRequestException, MetaDataAcquireFailedException, PermissionDeniedRequestException;

    /**
     * 获取任务状态信息
     * @param applicationId 任务id
     * @return 任务信息
     * @throws UnExpectedRequestException 参数异常
     */
    GeneralResponse<?> getApplicationStatus(String applicationId) throws UnExpectedRequestException;

    /**
     * 获取顶层任务状态信息
     * @param applicationId
     * @return
     */
    GeneralResponse<?> getApplicationDynamicStatus(String applicationId) throws UnExpectedRequestException;

    /**
     * 获取任务日志
     * @param request 请求
     * @return 任务日志
     * @throws UnExpectedRequestException 参数异常
     */
    GeneralResponse<?> getTaskLog(GetTaskLogRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Submit rules.
     *
     * @param jobId
     * @param ruleIds
     * @param partition
     * @param createUser
     * @param executionUser
     * @param nodeName
     * @param projectId
     * @param ruleGroupId
     * @param startupParam
     * @param clusterName
     * @param setFlag
     * @param execParams
     * @param execParamStr
     * @param runDate
     * @param invokeCode
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> submitRules(List<Long> ruleIds, StringBuffer partition, String createUser, String executionUser, String nodeName, Long projectId
        , Long ruleGroupId, String startupParam, String clusterName, String setFlag, Map<String, String> execParams, String execParamStr, StringBuffer runDate
        , Integer invokeCode);

    /**
     * 生成applicationInfo
     * @param createUser
     * @param executionUser
     * @param date
     * @param code
     * @return
     */
    Application generateApplicationInfo(String createUser, String executionUser, Date date, Integer code);

    /**
     * Group execution.
     * @param request
     * @param invokeCode
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<?> groupExecution(GroupExecutionRequest request, Integer invokeCode)
        throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * 项目调度
     * @param request
     * @param code
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> projectExecution(ProjectExecutionRequest request, Integer code)
        throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * 规则列表调度
     * @param request
     * @param invokeCode
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<?> ruleListExecution(RuleListExecutionRequest request, Integer invokeCode)
        throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * 获取application的结果状态
     * @param applicationId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> getApplicationResult(String applicationId) throws UnExpectedRequestException;

    /**
     * 杀死执行中的application
     * @param applicationId
     * @param executionUser
     * @return
     * @throws JobKillException
     * @throws UnExpectedRequestException
     * @throws ClusterInfoNotConfigException
     */
    GeneralResponse<?> killApplication(String applicationId, String executionUser)
        throws JobKillException, UnExpectedRequestException, ClusterInfoNotConfigException, PermissionDeniedRequestException;

    /**
     * Common execution for submit rules.
     * @param rules
     * @param partition
     * @param executionUser
     * @param nodeName
     * @param startupParam
     * @param clusterName
     * @param setFlag
     * @param execParams
     * @param newApplication
     * @param date
     * @param runDate
     * @return
     * @throws RuleVariableNotSupportException
     * @throws JobSubmitException
     * @throws RuleVariableNotFoundException
     * @throws ArgumentException
     * @throws ConvertException
     * @throws DataQualityTaskException
     * @throws TaskTypeException
     * @throws ClusterInfoNotConfigException
     * @throws SystemConfigException
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws IOException
     * @throws TaskNotExistException
     * @throws ParseException
     * @throws BothNullDatasourceException
     * @throws NullDatasourceException
     * @throws DataSourceMoveException
     * @throws DataSourceOverSizeException
     * @throws SemanticException
     * @throws org.apache.hadoop.hive.ql.parse.ParseException
     * @throws RightNullDatasourceException
     * @throws LeftNullDatasourceException
     */
    ApplicationTaskSimpleResponse commonExecution(List<Rule> rules, StringBuffer partition, String executionUser, String nodeName, String startupParam
        , String clusterName, String setFlag, Map<String, String> execParams, Application newApplication, Date date, StringBuffer runDate)
        throws RuleVariableNotSupportException, JobSubmitException, RuleVariableNotFoundException, ArgumentException, ConvertException, DataQualityTaskException, TaskTypeException, ClusterInfoNotConfigException, SystemConfigException, UnExpectedRequestException, MetaDataAcquireFailedException, IOException, TaskNotExistException, ParseException, BothNullDatasourceException, NullDatasourceException, DataSourceMoveException, DataSourceOverSizeException, SemanticException, org.apache.hadoop.hive.ql.parse.ParseException, RightNullDatasourceException, LeftNullDatasourceException;
}
