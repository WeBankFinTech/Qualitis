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
public enum ServiceInfoStatusEnum {
    /**
     * status
     * 1 running，4 stoped
     */
    RUNNING(1, "运行中", "RUNNING"),
    STOPED(4, "挂起", "STOPED");

    private Integer code;
    private String message;
    private String state;

    ServiceInfoStatusEnum(Integer code, String message, String state) {
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
