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
public enum InvokeTypeEnum {
    /**
     * Invke source type
     * 1 ui，2 bdp-client，3 工作流
     */
    UI_INVOKE(1, "系统触发"),
    BDP_CLIENT_API_INVOKE(2, "bdp-client触发"),
    FLOW_API_INVOKE(3, "工作流触发");

    private Integer code;
    private String message;

    InvokeTypeEnum(Integer code, String message) {
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
