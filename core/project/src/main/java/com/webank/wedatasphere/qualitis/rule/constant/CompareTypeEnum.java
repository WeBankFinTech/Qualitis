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
 * Enum in compareType of AlarmConfig
 * @author howeye
 */
public enum CompareTypeEnum {
    /**
     * 1 equal
     * 2 bigger
     * 3 smaller
     * 4 Bigger and equal to
     * 5 Smaller and equal to
     * 6 not equal
     */
    EQUAL(1, "等于"),
    BIGGER(2, "大于"),
    SMALLER(3, "小于"),
    BIGGER_EQUAL(4, "大于等于"),
    SMALLER_EQUAL(5, "小于等于"),
    NOT_EQUAL(6, "不等于"),
    ;

    private Integer code;
    private String message;

    CompareTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static String getCompareTypeName(Integer code) {
        for (CompareTypeEnum c : CompareTypeEnum.values()) {
            if (c.getCode().equals(code)) {
                return c.getMessage();
            }
        }
        return null;
    }

    public static Integer getCompareTypeCode(String compareTypeName) {
        for (CompareTypeEnum c : CompareTypeEnum.values()) {
            if (c.getMessage().equals(compareTypeName)) {
                return c.getCode();
            }
        }
        return null;
    }

}
