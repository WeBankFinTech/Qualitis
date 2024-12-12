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
 * @author allenzhou
 */
public enum RoleSystemTypeEnum {
    /**
     * type
     */
    ADMIN(1, "ADMIN"),
    PROJECTOR(2, "PROJECTOR"),
    DEPARTMENT_ADMIN(0, "DEPARTMENT_ADMIN");

    private Integer code;
    private String message;

    RoleSystemTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static RoleSystemTypeEnum fromCode(Integer roleType) {
        for (RoleSystemTypeEnum roleSystemTypeEnum: RoleSystemTypeEnum.values()) {
            if (roleSystemTypeEnum.code.equals(roleType)) {
                return roleSystemTypeEnum;
            }
        }
        return null;
    }
}
