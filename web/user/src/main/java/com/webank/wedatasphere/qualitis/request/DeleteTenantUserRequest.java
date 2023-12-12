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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;

/**
 * @author allenzhou
 */
public class DeleteTenantUserRequest {

    @JsonProperty("tenant_user_id")
    private Long tenantUserId;

    public DeleteTenantUserRequest() {
        // Default Constructor
    }

    public Long getTenantUserId() {
        return tenantUserId;
    }

    public void setTenantUserId(Long tenantUserId) {
        this.tenantUserId = tenantUserId;
    }

    public static void checkRequest(DeleteTenantUserRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        if (request.getTenantUserId() == null) {
            throw new UnExpectedRequestException("TenantUser id {&CAN_NOT_BE_NULL_OR_EMPTY}, request: " + request);
        }
    }

    @Override
    public String toString() {
        return "DeleteProxyUserRequest{" +
                "tenantUserId=" + tenantUserId +
                '}';
    }
}
