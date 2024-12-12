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

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enum in TemplateInputType of RuleTemplate
 *
 * @author howeye
 */
public enum TemplateInputTypeEnum {
    /**
     * Enum in TemplateInputType of RuleTemplate
     */
    FIXED_VALUE(1, "固定值", "Fixed value"),
    TABLE(3, "数据表", "Data table"),
    FIELD(4, "校验字段", "Field"),
    DATABASE(5, "数据库", "Database"),
    FIELD_CONCAT(6, "字段拼接", "Field concat"),
    REGEXP(7, "正则", "Regexp"),
    LIST(8, "枚举值", "List"),
    CONDITION(9, "基础过滤条件", "Condition"),

    /**
     * Provided for multi-table verification template
     */
    AND_CONCAT(10, "AND拼接", "And concat"),
    SOURCE_DB(11, "源数据库", "Source db"),
    SOURCE_TABLE(12, "源数据表", "Source table"),
    TARGET_DB(13, "目标数据库", "Target db"),
    TARGET_TABLE(14, "目标数据表", "Target table"),
    LEFT_STATEMENT(15, "join左表达式", "Left statement"),
    OPERATION(16, "join操作符", "Operation"),
    RIGHT_STATEMENT(17, "join右表达式", "Right statement"),
    SOURCE_FIELD(18, "join左字段", "Source field"),
    TARGET_FIELD(19, "join右字段", "Target field"),
    FRONT_CONDITION(20, "前置条件", "Front condition"),
    BEHIND_CONDITION(21, "后置条件", "Behind condition"),
    SOURCE_FIELDS(22, "来源字段", "Source fields"),
    TARGET_FIELDS(23, "目标字段", "Target fields"),

    /**
     * Provided for primary line repeat
     */
    FIELD_REPLACE_NULL_CONCAT(24, "替换空字段拼接", "Field replace null concat"),
    CONTRAST_TYPE(25, "比对方向", "Contrast type"),

    VALUE_RANGE(28, "数值范围", "Value range"),
    EXPRESSION(29, "表达式", "Expression"),
    SOURCE_BASIC_FILTER_CONDITIONS(30, "源基础过滤条件", "Source basic filter conditions"),
    TARGET_BASIC_FILTER_CONDITIONS(31, "目标基础过滤条件", "Target basic filter conditions"),
    CONNECT_FIELDS(32, "连接字段设置", "Connect fields"),
    COMPARISON_FIELD_SETTINGS(33, "比对字段设置", "Comparison field settings"),
    COMPARISON_RESULTS_FOR_FILTER(34, "比对结果过滤条件", "comparison results for filter"),

    FILTER_BY(35, "筛选方式", "Filter by"),


    //最大值、最小值 表达式
    MAXIMUM(36, "最大值", "Maximum"),
    INTERMEDIATE_EXPRESSION(37, "中间表达式", "Intermediate expression"),
    MINIMUM(38, "最小值", "Minimum"),
    STANDARD_VALUE_EXPRESSION(39, "标准值表达式", "standard value expression"),

    LEFT_COLLECT_SQL(40, "左表指标计算采集SQL", "left collector SQL for metric calculation"),
    RIGHT_COLLECT_SQL(41, "右表指标计算采集SQL", "right collector SQL for metric calculation");

    private Integer code;
    private String cnMessage;
    private String enMessage;

    TemplateInputTypeEnum(Integer code, String cnMessage, String enMessage) {
        this.code = code;
        this.cnMessage = cnMessage;
        this.enMessage = enMessage;
    }

    public static Map<String, Object> getTemplateData(Integer type) {
        for (TemplateInputTypeEnum c : TemplateInputTypeEnum.values()) {
            if (c.getCode().equals(type)) {
                Map<String, Object> item = new HashMap<>();
                item.put("code", c.code);
                item.put("cnMessage", c.cnMessage);
                item.put("enMessage", c.enMessage);
                return item;
            }
        }
        return Maps.newHashMap();
    }


    public static List<Map<String, Object>> getTemplateInputName(Integer code) {
        List<Map<String, Object>> hashMaps = new ArrayList<>();
        for (TemplateInputTypeEnum c : TemplateInputTypeEnum.values()) {
            if (c.getCode().equals(code)) {
                Map<String, Object> item = new HashMap<>();
                item.put("code", c.code);
                item.put("cnMessage", c.cnMessage);
                item.put("enMessage", c.enMessage);
                hashMaps.add(item);
            }
        }
        return hashMaps;
    }

    public Integer getCode() {
        return code;
    }

    public String getCnMessage() {
        return cnMessage;
    }

    public String getEnMessage() {
        return enMessage;
    }
}