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
 * Enum in file output unit.
 * @author howeye
 */
public enum FileOutputUnitEnum {
    /**
     * TB, GB, MB, KB
     */
    TB(1,"TB"),
    GB(2,"GB"),
    MB(3,"MB"),
    KB(4,"KB"),
    B(5,"B");

    private Integer code;
    private String message;

    FileOutputUnitEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static String fileOutputUnit(Integer code) {
        for (FileOutputUnitEnum c : FileOutputUnitEnum.values()) {
            if (c.getCode().equals(code)) {
                return c.getMessage();
            }
        }
        return null;
    }

    public static Integer fileOutputUnitCode(String message) {
        for (FileOutputUnitEnum c : FileOutputUnitEnum.values()) {
            if (c.getMessage().equals(message)) {
                return c.getCode();
            }
        }
        return null;
    }
}
