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
import com.webank.wedatasphere.qualitis.exception.ConvertException;
import com.webank.wedatasphere.qualitis.exception.DataQualityTaskException;
import com.webank.wedatasphere.qualitis.exception.RuleVariableNotFoundException;
import com.webank.wedatasphere.qualitis.exception.RuleVariableNotSupportException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * @author howeye
 */
public abstract class AbstractTemplateConverter {
    /**
     * Convert Task into code that can be executed.
     * @param dataQualityTask
     * @param date
     * @param setFlag
     * @param execParams
     * @param runDate
     * @param clusterType
     * @param dataSourceMysqlConnect
     * @return
     * @throws ConvertException
     * @throws DataQualityTaskException
     * @throws RuleVariableNotSupportException
     * @throws RuleVariableNotFoundException
     * @throws IOException
     * @throws UnExpectedRequestException
     */
    public abstract DataQualityJob convert(DataQualityTask dataQualityTask, Date date, String setFlag, Map<String, String> execParams, String runDate,
        String clusterType, Map<Long, Map> dataSourceMysqlConnect) throws ConvertException, DataQualityTaskException, RuleVariableNotSupportException, RuleVariableNotFoundException, IOException, UnExpectedRequestException;
}
