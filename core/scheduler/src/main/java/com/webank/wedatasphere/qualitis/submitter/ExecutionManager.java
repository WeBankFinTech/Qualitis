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
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.exception.JobSubmitException;
import com.webank.wedatasphere.qualitis.bean.TaskSubmitResult;
import com.webank.wedatasphere.qualitis.exception.ArgumentException;
import com.webank.wedatasphere.qualitis.exception.JobSubmitException;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author howeye
 */
public interface ExecutionManager {


    /**
     * Submit application
     * @param applicationId applicationId
     * @param rules
     * @param createTime
     * @param user
     * @param database
     * @param partition
     * @param date
     * @return
     * @throws ArgumentException
     * @throws TaskTypeException
     * @throws ConvertException
     * @throws DataQualityTaskException
     * @throws RuleVariableNotSupportException
     * @throws RuleVariableNotFoundException
     * @throws JobSubmitException
     * @throws ClusterInfoNotConfigException
     * @throws StatementShutdownException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    List<TaskSubmitResult> submitApplication(String applicationId, List<Rule> rules, String createTime, String user, String database, String partition, Date date, Application application) throws ArgumentException,
            TaskTypeException, ConvertException, DataQualityTaskException, RuleVariableNotSupportException, RuleVariableNotFoundException, JobSubmitException, ClusterInfoNotConfigException, ExecutionException, InterruptedException;

}
