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
public enum RuleTemplateTypeEnum {
    /**
     * 1 Single Table verification template
     * 2 Custom template
     * 3 Multi-table verification template
     * 4 File custom template
     */
    SINGLE_SOURCE_TEMPLATE(1, "单表模版"),
    CUSTOM(2, "自定义模版"),
    MULTI_SOURCE_TEMPLATE(3, "跨表模版"),
    FILE_COUSTOM(4, "文件自定义模版");

    private Integer code;
    private String message;

    RuleTemplateTypeEnum(Integer code, String message) {
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
