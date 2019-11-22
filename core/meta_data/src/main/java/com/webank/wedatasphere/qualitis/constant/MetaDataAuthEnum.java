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
public enum  MetaDataAuthEnum {
    /**
     * Enum in auth: cluster, database, table, field
     */
    CLUSTER_AUTH(1, "集群权限"),
    DB_AUTH(2, "数据库权限"),
    TABLE_AUTH(3, "表权限"),
    COLUMN_AUTH(4, "字段权限"),;

    private Integer code;
    private String message;

    MetaDataAuthEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
