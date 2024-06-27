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

package com.webank.wedatasphere.qualitis.project.constant;

/**
 * @author allenzhou
 */
public enum DiffRequestTypeEnum {
    /**
     * SYSTEM_INNER, DATASOURCE_ENV, SQL_REPLACEMENT, JSON_REPLACEMENT
     */
    SYSTEM_INNER(1, "system_inner_"),
    DATASOURCE_ENV(2, "datasource_env_"),
    SQL_REPLACEMENT(3, "sql_"),
    JSON_REPLACEMENT(4, "json_")
    ;

    private Integer code;
    private String prefixFile;

    DiffRequestTypeEnum(Integer code, String prefixFile) {
        this.code = code;
        this.prefixFile = prefixFile;
    }

    public Integer getCode() {
        return code;
    }

    public String getPrefixFile() {
        return prefixFile;
    }
}
