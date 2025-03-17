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

package com.webank.wedatasphere.qualitis.constants;

/**
 * @author allenzhou
 */
public enum WhiteListTypeEnum {
    /**
     * White list type
     */
    CHECK_ALERT_TABLE(1, "Check Alert Table"),
    BLACK_DGSM_PROJECT(2, "DGSM Project Black"),
    BLACK_DGSM_USER(3, "DGSM User Black"),
    BLACK_DGSM_DB(4, "DGSM Db Black"),
    BLACK_API_APPID(5, "API AppId Black"),
    BLACK_API_PROJECT(6, "API Project Black"),
    BLACK_API_USER(7, "API User Black")
    ;

    private Integer code;
    private String message;

    WhiteListTypeEnum(Integer code, String message) {
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
