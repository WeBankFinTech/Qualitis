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
public enum ApplicationStatusEnum {

    /**
     * application status enum
     */
    SUBMITTED(1, "已提交执行器", "SUBMITTED"),
    RUNNING(3, "运行中", "RUNNING"),
    FINISHED(4, "通过校验", "FINISHED"),
    FAILED(7, "失败", "FAILED"),
    NOT_PASS(8, "未通过校验", "NOT_PASS"),
    TASK_SUBMIT_FAILED(9, "任务初始化失败", "TASK_SUBMIT_FAILED"),
    SUCCESSFUL_CREATE_APPLICATION(10, "任务初始化成功", "SUCCESSFUL_CREATE_APPLICATION"),
    ARGUMENT_NOT_CORRECT(11, "参数错误", "ARGUMENT_NOT_CORRECT")
    ;

    private Integer code;
    private String message;
    private String state;

    ApplicationStatusEnum(Integer code, String message, String state) {
        this.code = code;
        this.message = message;
        this.state = state;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getState() {
        return state;
    }

}
