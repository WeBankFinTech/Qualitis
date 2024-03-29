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

package com.webank.wedatasphere.qualitis.metadata.exception;

import com.webank.wedatasphere.qualitis.response.GeneralResponse;

/**
 * @author v_wblwyan
 * @date 2018-12-07
 */
public class MetaDataAcquireFailedException extends Exception {
    private Integer status;

    public MetaDataAcquireFailedException(String message) {
        super(message);
        this.status = 400;
    }

    public MetaDataAcquireFailedException(String message, Integer status) {
        super(message);
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public GeneralResponse<Object> getResponse() {
        return new GeneralResponse<>(this.status + "", getMessage(), null);
    }
}
