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

package com.webank.wedatasphere.qualitis.submitter;


import com.webank.wedatasphere.qualitis.bean.TaskSubmitResult;
import com.webank.wedatasphere.qualitis.checkalert.entity.CheckAlert;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.exception.*;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public interface ExecutionManager {

    /**
     * Submit application.
     * @param rules
     * @param nodeName
     * @param createTime
     * @param user
     * @param ruleReplaceInfo
     * @param partition
     * @param date
     * @param application
     * @param cluster
     * @param startupParam
     * @param setFlag
     * @param execParams
     * @param runDate
     * @param splitBy
     * @param dataSourceMysqlConnect
     * @param tenantUserName
     * @param leftCols
     * @param rightCols
     * @param comelexCols
     * @param createUser
     * @return
     * @throws Exception
     */
    List<TaskSubmitResult> submitApplication(List<Rule> rules, String nodeName, String createTime, String user, Map<Long, Map<String, Object>> ruleReplaceInfo
            , StringBuilder partition, Date date, Application application, String cluster, String startupParam, String setFlag, Map<String, String> execParams, StringBuilder runDate
            , StringBuilder splitBy, Map<Long, List<Map<String, Object>>> dataSourceMysqlConnect, String tenantUserName, List<String> leftCols, List<String> rightCols, List<String> comelexCols, String createUser) throws Exception;

    /**
     * Kill application.
     * @param applicationInDb
     * @param user
     * @return
     * @throws JobKillException
     * @throws UnExpectedRequestException
     * @throws ClusterInfoNotConfigException
     */
    GeneralResponse<Integer> killApplication(Application applicationInDb, String user) throws JobKillException, UnExpectedRequestException, ClusterInfoNotConfigException;

    /**
     * File rule job.
     * @param fileRules
     * @param submitTime
     * @param application
     * @param user
     * @param clusterName
     * @param runDate
     * @param ruleReplaceInfo
     * @return
     * @throws Exception
     */
    TaskSubmitResult executeFileRule(List<Rule> fileRules, String submitTime, Application application, String user, String clusterName, StringBuilder runDate
        , Map<Long, Map<String, Object>> ruleReplaceInfo) throws Exception;

    /**
     * File rule shell job.
     * @param fileRules
     * @param submitTime
     * @param application
     * @param user
     * @param clusterName
     * @param runDate
     * @param ruleReplaceInfo
     * @param startupParam
     * @param engineReuse
     * @param engineType
     * @return
     * @throws Exception
     */
    List<TaskSubmitResult> executeFileRuleWithShell(List<Rule> fileRules, String submitTime, Application application, String user, String clusterName
        , String runDate, Map<Long, Map<String, Object>> ruleReplaceInfo, String startupParam, Boolean engineReuse, String engineType) throws Exception;

    /**
     * Execute check alert.
     * @param clusterInfo
     * @param dbAndTables
     * @param columns
     * @param currentCheckAlert
     * @param saveApplication
     * @param startupParam
     * @param engineReuse
     * @param engineType
     * @return
     * @throws Exception
     */
    TaskSubmitResult executeCheckAlert(ClusterInfo clusterInfo, String[] dbAndTables, List<String> columns, CheckAlert currentCheckAlert,
        Application saveApplication,
        String startupParam, Boolean engineReuse, String engineType) throws Exception;
}
