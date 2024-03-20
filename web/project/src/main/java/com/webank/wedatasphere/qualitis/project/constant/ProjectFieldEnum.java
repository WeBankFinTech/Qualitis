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
public enum ProjectFieldEnum {
    /**
     * Monthly, weekly, day and fixed name
     * Ring growth, year on year
     */
    CHINESE_NAME(1,"项目中文名称", "Project chinese name"),
    PROJECT_LABEL(3,"项目标签", "Project label"),
    PROJECT_DESC(4,"项目介绍", "Project desc"),
    EN_NAME(2,"项目英文名称", "Project name"),
    SUB_SYSTEM(5,"子系统", "Sub system")
    ;

    private Integer code;
    private String zhMessage;
    private String enMessage;

    ProjectFieldEnum(Integer code, String zhMessage, String enMessage) {
        this.code = code;
        this.zhMessage = zhMessage;
        this.enMessage = enMessage;
    }

    public Integer getCode() {
        return code;
    }

    public String getZhMessage() {
        return zhMessage;
    }

    public String getEnMessage() {
        return enMessage;
    }
    public static String getProjectFieldName(Integer code) {
        for (ProjectFieldEnum c : ProjectFieldEnum.values()) {
            if (c.getCode().equals(code)) {
                return c.getZhMessage();
            }
        }
        return null;
    }

    public static String getProjectFieldName(Integer code, String local) {
        for (ProjectFieldEnum c : ProjectFieldEnum.values()) {
            if (c.getCode().equals(code)) {
                if ("en_US".equals(local)) {
                    return c.getEnMessage();
                } else {
                    return c.getZhMessage();
                }
            }
        }
        return null;
    }

}
