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

package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author allenzhou
 */
public class CopyRuleRequest {
    @JsonProperty("target_project_id")
    private Long targetProjectId;
    @JsonProperty("target_rule_group_id")
    private Long targetRuleGroupId;
    @JsonProperty("source_rule_group_id")
    private Long sourceRuleGroupId;
    @JsonProperty("source_rule_id_list")
    private List<Long> sourceRuleIdList;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("version")
    private String version;
    @JsonProperty("old_version")
    private String oldVersion;


    public CopyRuleRequest() {
        // Default Constructor
    }

    public Long getTargetProjectId() {
        return targetProjectId;
    }

    public void setTargetProjectId(Long targetProjectId) {
        this.targetProjectId = targetProjectId;
    }

    public Long getTargetRuleGroupId() {
        return targetRuleGroupId;
    }

    public void setTargetRuleGroupId(Long targetRuleGroupId) {
        this.targetRuleGroupId = targetRuleGroupId;
    }

    public Long getSourceRuleGroupId() {
        return sourceRuleGroupId;
    }

    public void setSourceRuleGroupId(Long sourceRuleGroupId) {
        this.sourceRuleGroupId = sourceRuleGroupId;
    }

    public List<Long> getSourceRuleIdList() {
        return sourceRuleIdList;
    }

    public void setSourceRuleIdList(List<Long> sourceRuleIdList) {
        this.sourceRuleIdList = sourceRuleIdList;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOldVersion() {
        return oldVersion;
    }

    public void setOldVersion(String oldVersion) {
        this.oldVersion = oldVersion;
    }

    public static void checkRequest(CopyRuleRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkObject(request.getCreateUser(), "Create user");

        if (request.getSourceRuleGroupId() == null && CollectionUtils.isEmpty(request.getSourceRuleIdList())) {
            throw new UnExpectedRequestException("Source rule info" + " {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }
}
