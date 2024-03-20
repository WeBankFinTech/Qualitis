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
 * @author allenzhou
 */
public enum OperateTypeEnum {
    /**
     * Type of project
     */
    UNAUTHORIZE_PROJECT(12, "Unauthorized Project", "取消授权项目"),
    AUTHORIZE_PROJECT(11, "Authorized Project", "授权项目"),
    SUBMIT_PROJECT(10, "Submit Project", "提交项目"),
    CREATE_PROJECT(1, "Create Project", "创建项目"),
    IMPORT_PROJECT(2, "Import Project", "导入项目"),
    EXPORT_PROJECT(3, "Export Project","导出项目"),
    DELETE_PROJECT(4, "Create Project", "删除项目"),
    MODIFY_PROJECT(5, "Modify Project", "修改项目"),
    CREATE_RULES(6,"Create Project","创建规则"),
    MODIFY_RULES(7,"Modify Project","修改规则"),
    DELETE_RULES(8,"Delete Project","删除规则"),
    ;

    private Integer code;
    private String message;
    private String name;

    OperateTypeEnum(Integer code, String message, String name) {
        this.code = code;
        this.message = message;
        this.name = name;
    }

    public static OperateTypeEnum fromCode(Integer code) {
        for (OperateTypeEnum operateTypeEnum : OperateTypeEnum.values()) {
            if (operateTypeEnum.getCode().equals(code)) {
                return operateTypeEnum;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
