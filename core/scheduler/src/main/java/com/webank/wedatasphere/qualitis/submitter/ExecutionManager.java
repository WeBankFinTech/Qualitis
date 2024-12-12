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
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.exception.*;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.exception.JobSubmitException;
import com.webank.wedatasphere.qualitis.exception.ArgumentException;

import java.io.IOException;
import java.text.ParseException;
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
     * @param database
     * @param partition
     * @param date
     * @param application
     * @param cluster
     * @param startupParam
     * @param setFlag
     * @param execParams
     * @param runDate
     * @param dataSourceMysqlConnect
     * @return
     * @throws ArgumentException
     * @throws TaskTypeException
     * @throws ConvertException
     * @throws DataQualityTaskException
     * @throws RuleVariableNotSupportException
     * @throws RuleVariableNotFoundException
     * @throws JobSubmitException
     * @throws ClusterInfoNotConfigException
     * @throws IOException
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws ParseException
     */
    List<TaskSubmitResult> submitApplication(List<Rule> rules, String nodeName, String createTime, String user,
        String database, StringBuffer partition, Date date, Application application, String cluster, String startupParam, String setFlag,
        Map<String, String> execParams,
        StringBuffer runDate, Map<Long, Map> dataSourceMysqlConnect) throws ArgumentException
        , TaskTypeException, ConvertException, DataQualityTaskException, RuleVariableNotSupportException, RuleVariableNotFoundException, JobSubmitException, ClusterInfoNotConfigException
        , IOException, UnExpectedRequestException, MetaDataAcquireFailedException, ParseException;

    /**
     * File rule job.
     * @param fileRules
     * @param submitTime
     * @param application
     * @param user
     * @param clusterName
     * @param runDate
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws ParseException
     */
    TaskSubmitResult executeFileRule(List<Rule> fileRules, String submitTime, Application application, String user, String clusterName,
        StringBuffer runDate)
        throws UnExpectedRequestException, MetaDataAcquireFailedException, ParseException;

    /**
     * Kill application
     * @param applicationInDb
     * @param user
     * @return
     * @throws JobKillException
     * @throws UnExpectedRequestException
     * @throws ClusterInfoNotConfigException
     */
    GeneralResponse<?> killApplication(Application applicationInDb, String user)
        throws JobKillException, UnExpectedRequestException, ClusterInfoNotConfigException;
}
