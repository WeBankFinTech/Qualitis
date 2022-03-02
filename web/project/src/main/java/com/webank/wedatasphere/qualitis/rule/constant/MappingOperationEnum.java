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
public enum MappingOperationEnum {
    /**
     * Join Operation Enum
     */
    EQUAL(1, "相等", "="),
    NOT_EQUAL(2, "不相等", "!="),
    GREATER(3, "大于", ">"),
    GREATER_EQUAL(4, "大于等于", ">="),
    LESS(5, "小于", "<"),
    LESS_EQUAL(6, "小于等于", "<="),
    ;

    private Integer code;
    private String message;
    private String symbol;

    MappingOperationEnum(Integer code, String message, String symbol) {
        this.code = code;
        this.message = message;
        this.symbol = symbol;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getSymbol() {
        return symbol;
    }

    public static MappingOperationEnum getByCode(Integer code) {
        for (MappingOperationEnum tmp : MappingOperationEnum.values()) {
            if (tmp.getCode().equals(code)) {
                return tmp;
            }
        }
        return null;
    }

    public static Integer getOperationCode(String symbol) {
        for (MappingOperationEnum tmp : MappingOperationEnum.values()) {
            if (tmp.getSymbol().equals(symbol)) {
                return tmp.getCode();
            }
        }
        return null;
    }
}
