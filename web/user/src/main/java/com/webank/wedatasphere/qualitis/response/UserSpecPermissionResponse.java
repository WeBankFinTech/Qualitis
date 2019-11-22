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
import com.webank.wedatasphere.qualitis.entity.UserSpecPermission;
import com.webank.wedatasphere.qualitis.entity.UserSpecPermission;

/**
 * @author howeye
 */
public class UserSpecPermissionResponse {

    private String uuid;
    @JsonProperty("user_id")
    private long userId;
    @JsonProperty("permission_id")
    private long permissionId;

    public UserSpecPermissionResponse() {
        // Default Constructor
    }

    public UserSpecPermissionResponse(UserSpecPermission userSpecPermission) {
        this.uuid = userSpecPermission.getId();
        this.userId = userSpecPermission.getUser().getId();
        this.permissionId = userSpecPermission.getPermission().getId();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(long permissionId) {
        this.permissionId = permissionId;
    }

    @Override
    public String toString() {
        return "UserSpecPermissionResponse{" +
                "uuid='" + uuid + '\'' +
                ", userId=" + userId +
                ", permissionId=" + permissionId +
                '}';
    }
}
