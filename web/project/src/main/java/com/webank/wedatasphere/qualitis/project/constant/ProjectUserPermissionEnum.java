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

package com.webank.wedatasphere.qualitis.project.constant;

/**
 * @author howeye
 */
public enum ProjectUserPermissionEnum {
    /**
     * 创建者（管理员： 编辑，删除，执行，查看），开发者（编辑），运维者（执行），业务员（查看）
     */
    CREATOR(1, "创建者"),
    DEVELOPER(2, "开发者"),
    OPERATOR(3, "运维者"),
    BUSSMAN(4, "业务员");

    private Integer code;
    private String message;

    ProjectUserPermissionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
