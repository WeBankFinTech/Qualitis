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

import java.util.Arrays;
import java.util.List;

/**
 * Enum in checkTemplate of AlarmConfig
 * @author howeye
 */
public enum CheckTemplateEnum {
    /**
     * Monthly, weekly, day and fixed name
     */
    MONTH_FLUCTUATION(1,"月波动", Arrays.asList(Number.class)),
    WEEK_FLUCTUATION(2,"周波动", Arrays.asList(Number.class)),
    DAY_FLUCTUATION(3,"日波动", Arrays.asList(Number.class)),
    FIXED_VALUE(4,"固定值", Arrays.asList(Number.class)),
        ;

    private Integer code;
    private String message;
    private List<Class> classes;

    CheckTemplateEnum(Integer code, String message, List<Class> classes) {
        this.code = code;
        this.message = message;
        this.classes = classes;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<Class> getClasses() {
        return classes;
    }

    public static String getCheckTemplateName(Integer code) {
        for (CheckTemplateEnum c : CheckTemplateEnum.values()) {
            if (c.getCode().equals(code)) {
                return c.getMessage();
            }
        }
        return null;
    }

    public static Integer getCheckTemplateCode(String checkTemplateName) {
        for (CheckTemplateEnum c : CheckTemplateEnum.values()) {
            if (c.getMessage().equals(checkTemplateName)) {
                return c.getCode();
            }
        }
        return null;
    }
}
