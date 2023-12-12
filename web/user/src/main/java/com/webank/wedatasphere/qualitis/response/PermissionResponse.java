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

package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.entity.Permission;

/**
 * @author howeye
 */
public class PermissionResponse extends BaseResponse {

    @JsonProperty("permission_id")
    private Long permissionId;
    private String url;
    private String method;
    private String splitName;

    public PermissionResponse() {
        // Default Constructor
    }

    public PermissionResponse(Permission permission) {
        this.permissionId = permission.getId();
        this.url = permission.getUrl();
        this.method = permission.getMethod();
        //method + ":" + url
        this.splitName = permission.getMethod() + ":" + permission.getUrl();
        this.createTime = permission.getCreateTime();
        this.createUser = permission.getCreateUser();
        this.modifyUser = permission.getModifyUser();
        this.modifyTime = permission.getModifyTime();
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getSplitName() {
        return splitName;
    }

    public void setSplitName(String splitName) {
        this.splitName = splitName;
    }

    @Override
    public String toString() {
        return "PermissionResponse{" +
                "permissionId=" + permissionId +
                ", url='" + url + '\'' +
                ", method='" + method + '\'' +
                '}';
    }
}
