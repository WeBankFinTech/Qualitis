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

package com.webank.wedatasphere.qualitis.rule.constant;

/**
 * @author howeye
 */
public enum RuleTypeEnum {
    /**
     * Rule type, including rule using template and custom rule
     */
    SINGLE_TEMPLATE_RULE(1, "Single Template rule"),
    CUSTOM_RULE(2, "Custom rule"),
    MULTI_TEMPLATE_RULE(3, "Multi Template rule"),
    FILE_TEMPLATE_RULE(4, "File Template rule")
    ;

    private Integer code;
    private String message;

    RuleTypeEnum(Integer code, String message) {
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
