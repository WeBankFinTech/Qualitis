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
 * Enum in checkTemplate of AlarmConfig
 * @author howeye
 */
public enum CheckTemplateEnum {
    /**
     * Monthly, weekly, day and fixed name
     * Ring growth, year on year
     */
    MONTH_FLUCTUATION(1,"月波动", "Month Fluctuation"),
    WEEK_FLUCTUATION(2,"周波动", "Week Fluctuation"),
    DAY_FLUCTUATION(3,"日波动", "Daily Fluctuation"),
    FIXED_VALUE(4,"固定值", "Fix Value"),
    FULL_YEAR_RING_GROWTH(5,"年环比", "Full Year Ring Growth"),
    HALF_YEAR_GROWTH(6,"半年环比", "Half Year Ring Growth"),
    SEASON_RING_GROWTH(7,"季环比", "Season Ring Growth"),
    MONTH_RING_GROWTH(8,"月环比", "Month Ring Growth"),
    WEEK_RING_GROWTH(9,"周环比", "Week Ring Growth"),
    DAY_RING_GROWTH(10,"日环比", "Day Ring Growth"),
    HOUR_RING_GROWTH(11,"时环比", "Hour Ring Growth"),
    YEAR_ON_YEAR(12,"月同比", "YEAR ON YEAR");

    private Integer code;
    private String zhMessage;
    private String enMessage;

    CheckTemplateEnum(Integer code, String zhMessage, String enMessage) {
        this.code = code;
        this.zhMessage = zhMessage;
        this.enMessage = enMessage;
    }

    public Integer getCode() {
        return code;
    }

    public String getZhMessage() {
        return zhMessage;
    }

    public String getEnMessage() {
        return enMessage;
    }

    public static String getCheckTemplateName(Integer code) {
        for (CheckTemplateEnum c : CheckTemplateEnum.values()) {
            if (c.getCode().equals(code)) {
                return c.getZhMessage();
            }
        }
        return null;
    }

    public static Integer getCheckTemplateCode(String checkTemplateName) {
        for (CheckTemplateEnum c : CheckTemplateEnum.values()) {
            if (c.getZhMessage().equals(checkTemplateName)) {
                return c.getCode();
            }
        }
        return null;
    }

    public static String getCheckTemplateName(Integer code, String local) {
        for (CheckTemplateEnum c : CheckTemplateEnum.values()) {
            if (c.getCode().equals(code)) {
                if ("en_US".equals(local)) {
                    return c.getEnMessage();
                } else {
                    return c.getZhMessage();
                }
            }
        }
        return null;
    }

    public static Integer getCheckTemplateCode(String checkTemplateName, String local) {
        for (CheckTemplateEnum c : CheckTemplateEnum.values()) {
            if ("en_US".equals(local)) {
                if (c.getEnMessage().equals(checkTemplateName)) {
                    return c.getCode();
                }
            } else {
                if (c.getZhMessage().equals(checkTemplateName)) {
                    return c.getCode();
                }
            }
        }
        return null;
    }
}
