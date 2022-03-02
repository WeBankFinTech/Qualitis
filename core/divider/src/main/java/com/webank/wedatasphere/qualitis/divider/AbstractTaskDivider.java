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

package com.webank.wedatasphere.qualitis.divider;

import com.webank.wedatasphere.qualitis.bean.DataQualityTask;
import com.webank.wedatasphere.qualitis.exception.ArgumentException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;

import java.util.Date;
import java.util.List;

/**
 * @author howeye
 */
public abstract class AbstractTaskDivider {
    /**
     * Divided rules into multi-task.
     * @param rules
     * @param applicationId
     * @param createTime
     * @param partition
     * @param date
     * @param database
     * @param user
     * @param threshold
     * @return
     * @throws ArgumentException
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    public abstract List<DataQualityTask> divide(List<Rule> rules, String applicationId, String createTime, String partition, Date date,
        String database, String user, Integer threshold) throws ArgumentException, UnExpectedRequestException, MetaDataAcquireFailedException;
}