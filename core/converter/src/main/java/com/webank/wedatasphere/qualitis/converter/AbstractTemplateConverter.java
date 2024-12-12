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

package com.webank.wedatasphere.qualitis.converter;

import com.webank.wedatasphere.qualitis.bean.DataQualityJob;
import com.webank.wedatasphere.qualitis.bean.DataQualityTask;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public abstract class AbstractTemplateConverter {

    /**
     * Convert Task into code that can be executed.
     *
     * @param dataQualityTask
     * @param date
     * @param setFlag
     * @param execParams
     * @param runDate
     * @param runToday
     * @param clusterType
     * @param dataSourceMysqlConnect
     * @param user
     * @param leftCols
     * @param rightCols
     * @param comelexCols
     * @param createUser
     * @param projectId
     * @return
     * @throws Exception
     */
    public abstract DataQualityJob convert(DataQualityTask dataQualityTask, Date date, String setFlag, Map<String, String> execParams, String runDate,
                                           String runToday, String clusterType, Map<Long, List<Map<String, Object>>> dataSourceMysqlConnect, String user, List<String> leftCols, List<String> rightCols,
                                           List<String> comelexCols, String createUser, Long projectId) throws Exception;
}
