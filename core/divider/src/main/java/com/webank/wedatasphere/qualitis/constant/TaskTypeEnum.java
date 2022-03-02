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

package com.webank.wedatasphere.qualitis.constant;

/**
 * @author howeye
 */
public enum TaskTypeEnum {
    /**
     * 1 SQL Task，2 Java Task，3 Spark Task，4 Python Task 5 Mix Task
     */
    SQL_TASK(1, "SQL TASK"),
    JAVA_TASK(2, "JAVA TASK"),
    SPARK_TASK(3, "SPARK TASK"),
    PYTHON_TASK(4, "PYTHON TASK"),
    MIX_TASK(5, "MIX TASK");

    private Integer code;
    private String message;

    TaskTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public final Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
