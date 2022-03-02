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

public enum BlackListFilterTypeEnum {
    /**
     * 1 contain
     * 2 begin with
     * 3 end with
     */
    CONTAINS(1, "Contains string"),
    BEGIN_WITH(2, "Begin with string"),
    END_WITH(3, "End with string"),
    SAME_TABLE(4, "Same table"),
    REG_TABLE(5, "Regression"),
    ;

    private Integer code;
    private String message;

    BlackListFilterTypeEnum(Integer code, String message) {
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
