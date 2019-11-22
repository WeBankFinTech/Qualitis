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

package com.webank.wedatasphere.qualitis.request;

import com.webank.wedatasphere.qualitis.constant.ApplicationStatusEnum;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.constant.ApplicationStatusEnum;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;

/**
 * @author howeye
 */
public class FilterStatusRequest {

    private Integer status;
    private Integer page;
    private Integer size;

    public FilterStatusRequest() {
        this.page = 0;
        this.size = 5;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public static void checkRequest(FilterStatusRequest request) throws UnExpectedRequestException {
        if (request.getStatus() != null) {
            for (ApplicationStatusEnum t : ApplicationStatusEnum.values()) {
                if (t.getCode().equals(request.getStatus())) {
                    return;
                }
            }

            throw new UnExpectedRequestException("{&APPLICATION_STATUS_DOES_NOT_SUPPORTED}");
        }
    }
}
