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
 * @author allenzhou
 */

public enum UdfImplTypeEnum {
    /**
     * 1 Scala
     * 2 Python
     * 3 Jar
     */
    SCALA(1, "Scala"),
    PYTHON(2, "Python"),
    JAR(3, "Jar")
    ;

    private Integer code;
    private String message;

    UdfImplTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(Integer code) {
        for (UdfImplTypeEnum udfImplTypeEnum : UdfImplTypeEnum.values()) {
            if (udfImplTypeEnum.code.equals(code)) {
                return udfImplTypeEnum.message;
            }
        }
        return UdfImplTypeEnum.JAR.getMessage();
    }

    public static Integer getCode(String message) {
        for (UdfImplTypeEnum udfImplTypeEnum : UdfImplTypeEnum.values()) {
            if (udfImplTypeEnum.message.equals(message)) {
                return udfImplTypeEnum.code;
            }
        }
        return UdfImplTypeEnum.JAR.getCode();
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
