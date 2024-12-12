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
public enum ProjectOperateEnum {
    /**
     * Type of project
     */
    ADD(1, "add", "添加"),
    MODIFY(2, "modify", "编辑"),
    EXECUTION(3, "execute", "执行"),
    DOWNLOAD(4, "download", "下载"),
    UPLOAD(5, "upload", "上传"),
    ;

    private Integer code;
    private String message;
    private String zhMessage;

    ProjectOperateEnum(Integer code, String message, String zhMessage) {
        this.code = code;
        this.message = message;
        this.zhMessage = zhMessage;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getZhMessage() {
        return zhMessage;
    }

    public void setZhMessage(String zhMessage) {
        this.zhMessage = zhMessage;
    }
}
