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
public enum AlarmConfigStatusEnum {
    /**
     * 1 Pass verification，2 Not pass verification， 3 Not verified
     */
    PASS(1, "通过校验"),
    NOT_PASS(2, "未通过校验"),
    NOT_CHECK(3, "未校验");

    private Integer code;
    private String message;

    AlarmConfigStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(Integer status) {
        for (AlarmConfigStatusEnum alarmConfigStatusEnum : AlarmConfigStatusEnum.values()) {
            if (alarmConfigStatusEnum.getCode().equals(status)) {
                return alarmConfigStatusEnum.getMessage();
            }
        }
        return "Not support status code";
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
