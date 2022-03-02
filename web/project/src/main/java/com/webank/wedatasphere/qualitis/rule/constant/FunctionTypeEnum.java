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

public enum FunctionTypeEnum {
    /**
     * Function Enum
     */
    SUM_FUNCTION(1, "sum", "Sum Function"),
    AVG_FUNCTION(2, "avg", "Avg Function"),
    COUNT_FUNCTION(3, "count", "Count Function"),
    MAX_FUNCTION(4, "max", "Max Function"),
    MIN_FUNCTION(5, "min", "Min Function"),
    ;

    private Integer code;
    private String function;
    private String message;

    FunctionTypeEnum(Integer code, String function, String message) {
        this.code = code;
        this.function = function;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getFunction() {
        return function;
    }

    public String getMessage() {
        return message;
    }

    public static FunctionTypeEnum getByCode(Integer code) {
        for (FunctionTypeEnum e : FunctionTypeEnum.values()) {
            if (code.equals(e.getCode())) {
                return e;
            }
        }
        return null;
    }

    public static Integer getFunctionTypeByName(String functionName) {
        for (FunctionTypeEnum e : FunctionTypeEnum.values()) {
            if (functionName.equals(e.getFunction())) {
                return e.getCode();
            }
        }
        return null;
    }

    public static String getFunctionByCode(Integer code) {
        for (FunctionTypeEnum e : FunctionTypeEnum.values()) {
            if (code.equals(e.getCode())) {
                return e.getFunction();
            }
        }
        return null;
    }
}
