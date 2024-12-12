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

import com.webank.wedatasphere.qualitis.bean.DataQualityTask;
import com.webank.wedatasphere.qualitis.constant.TaskTypeEnum;
import com.webank.wedatasphere.qualitis.exception.TaskTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author howeye
 */
@Component
public class TemplateConverterFactory {

    @Autowired
    private SqlTemplateConverter sqlTemplateConverter;

    public AbstractTemplateConverter getConverter(DataQualityTask dataQualityTask) throws TaskTypeException {
        if (TaskTypeEnum.SQL_TASK.getCode().equals(dataQualityTask.getTaskType())) {
            return sqlTemplateConverter;
        } else if (TaskTypeEnum.JAVA_TASK.getCode().equals(dataQualityTask.getTaskType())) {
            throw new TaskTypeException("Java task is not supported");
//            return new JavaTemplateConverter();
        } else if (TaskTypeEnum.SPARK_TASK.getCode().equals(dataQualityTask.getTaskType())) {
            throw new TaskTypeException("Spark task is not supported");
//            return new SparkTemplateConverter();
        } else if (TaskTypeEnum.PYTHON_TASK.getCode().equals(dataQualityTask.getTaskType())) {
            throw new TaskTypeException("Python task is not supported");
//            return new PythonTemplateConverter();
        } else if (TaskTypeEnum.MIX_TASK.getCode().equals(dataQualityTask.getTaskType())) {
            throw new TaskTypeException("Mix task is not supported");
//            return new MixTemplateConverter();
        } else {
            throw new TaskTypeException("task type: [" + dataQualityTask.getTaskType() + "] is not supported");
        }

    }

}
