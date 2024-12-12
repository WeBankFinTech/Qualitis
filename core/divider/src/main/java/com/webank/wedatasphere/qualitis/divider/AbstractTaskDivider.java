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
import java.util.Map;

/**
 * @author howeye
 */
public abstract class AbstractTaskDivider {
    /**
     * Divided rules into multi-task.
     *
     * @param clusterName
     * @param datasourceIndex
     * @param rules
     * @param applicationId
     * @param createTime
     * @param partition
     * @param date
     * @param databaseMap
     * @param dataSourceMysqlConnect
     * @param user
     * @param threshold
     * @param splitBy
     * @param startupParam
     * @return
     * @throws ArgumentException
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    public abstract List<DataQualityTask> divide(String clusterName, Integer datasourceIndex, List<Rule> rules, String applicationId,
        String createTime,
        String partition, Date date,
        Map<Long, Map<String, Object>> databaseMap,
        Map<Long, List<Map<String, Object>>> dataSourceMysqlConnect, String user,
        Integer threshold, String splitBy, String startupParam) throws ArgumentException, UnExpectedRequestException, MetaDataAcquireFailedException;
}