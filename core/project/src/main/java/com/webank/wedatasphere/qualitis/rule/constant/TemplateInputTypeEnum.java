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
 * Enum in TemplateInputType of RuleTemplate
 * @author howeye
 */
public enum TemplateInputTypeEnum {
    /**
     * Enum in TemplateInputType of RuleTemplate
     */
    FIXED_VALUE(1, "固定值"),
    TABLE(3, "数据表"),
    FIELD(4, "字段"),
    DATABASE(5, "数据库"),
    FIELD_CONCAT(6, "字段拼接"),
    REGEXP(7, "正则"),
    LIST(8, "数组"),
    CONDITION(9, "条件"),

    /**
     * Provided for multi-table verification template
     */
    AND_CONCAT(10, "AND拼接"),
    SOURCE_DB(11, "来源数据库"),
    SOURCE_TABLE(12, "来源表"),
    TARGET_DB(13, "目标数据库"),
    TARGET_TABLE(14, "目标表"),
    LEFT_STATEMENT(15, "join左表达式"),
    OPERATION(16, "join操作符"),
    RIGHT_STATEMENT(17, "join右表达式"),
    SOURCE_FIELD(18, "join左字段"),
    TARGET_FIELD(19, "join右字段"),
    FRONT_CONDITION(20, "前置条件"),
    BEHIND_CONDITION(21, "后置条件"),
    SOURCE_FIELDS(22, "来源字段"),
    TARGET_FIELDS(23, "目标字段"),

    /**
     * Provided for primary line repeat
     */
    FIELD_REPLACE_NULL_CONCAT(24, "替换空字段拼接"),
    ;

    private Integer code;
    private String message;

    TemplateInputTypeEnum(Integer code, String message) {
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
