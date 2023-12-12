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
 * @author allenzhou
 */
public enum OptTypeEnum {
    /**
     * Opt type
     */
    CHECK_DF(1, "checkDF"),
    STATISTIC_DF(2, "statisticDF"),
    SCHEMAS(3, "schemas"),
    NEW_SCHEMAS(4, "newSchemas"),
    ORIGINAL_STATISTIC_DF(5, "originalStatisticDF"),
    LEFT_JOIN_STATISTIC_DF(6, "leftOriginalStatisticDF"),
    RIGHT_JOIN_STATISTIC_DF(7, "rightOriginalStatisticDF");

    private Integer code;
    private String message;

    OptTypeEnum(Integer code, String message) {
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
