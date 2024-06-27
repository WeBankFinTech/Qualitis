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

package com.webank.wedatasphere.qualitis.project.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.project.constant.DiffRequestTypeEnum;

/**
 * @author allenzhou
 */
public class DiffVariableRequest {
    @JsonProperty("type")
    private Integer type;
    @JsonProperty("name")
    private String name;
    @JsonProperty("value")
    private String value;

    public DiffVariableRequest() {
        // Default Constructor
    }

    public DiffVariableRequest(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public DiffVariableRequest(String fileName, String name, String value) {
        if (fileName.startsWith(DiffRequestTypeEnum.SYSTEM_INNER.getPrefixFile())) {
            this.type = DiffRequestTypeEnum.SYSTEM_INNER.getCode();
        } else if (fileName.startsWith(DiffRequestTypeEnum.DATASOURCE_ENV.getPrefixFile())) {
            this.type = DiffRequestTypeEnum.DATASOURCE_ENV.getCode();
        } else if (fileName.startsWith(DiffRequestTypeEnum.SQL_REPLACEMENT.getPrefixFile())) {
            this.type = DiffRequestTypeEnum.SQL_REPLACEMENT.getCode();
        } else if (fileName.startsWith(DiffRequestTypeEnum.JSON_REPLACEMENT.getPrefixFile())) {
            this.type = DiffRequestTypeEnum.JSON_REPLACEMENT.getCode();
        }
        this.name = name;
        this.value = value;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
