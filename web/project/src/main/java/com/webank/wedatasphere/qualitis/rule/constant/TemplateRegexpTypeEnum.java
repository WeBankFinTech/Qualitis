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
public enum TemplateRegexpTypeEnum {
    /**
     * 1.Date Regex
     * 2.Number Regex
     * 3.Identity Regex
     */
    DATE(1, "日期格式"),
    NUMBER(2, "数值格式"),
    IDENTITY(3, "身份证");


    private Integer code;
    private String message;

    TemplateRegexpTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static boolean contains(Integer code) {
        for (TemplateRegexpTypeEnum t : TemplateRegexpTypeEnum.values()) {
            if (t.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}
