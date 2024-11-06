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

package com.webank.wedatasphere.qualitis.project.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.ProjectTransportTypeEnum;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author howeye
 */
public class DownloadProjectRequest {

    @JsonProperty("project_ids")
    private List<Long> projectId;

    @JsonProperty("download_type")
    private Integer downloadType;

    @JsonProperty("git_repo")
    private String gitRepo;
    @JsonProperty("git_type")
    private Integer gitType;
    @JsonProperty("git_branch")
    private String gitBranch;
    @JsonProperty("git_root_dir")
    private String gitRootDir;

    @JsonProperty("diff_variables")
    private List<DiffVariableRequest> diffVariableRequestList;

    @JsonProperty("rule_ids")
    private List<Long> ruleIds;

    @JsonProperty("rule_names")
    private List<String> ruleNames;

    @JsonProperty("operate_user")
    private String operateUser;

    public DownloadProjectRequest() {
        // Default Constructor
    }

    public List<Long> getProjectId() {
        return projectId;
    }

    public void setProjectId(List<Long> projectId) {
        this.projectId = projectId;
    }

    public Integer getDownloadType() {
        return downloadType;
    }

    public void setDownloadType(Integer downloadType) {
        this.downloadType = downloadType;
    }

    public String getGitRepo() {
        return gitRepo;
    }

    public void setGitRepo(String gitRepo) {
        this.gitRepo = gitRepo;
    }

    public Integer getGitType() {
        return gitType;
    }

    public void setGitType(Integer gitType) {
        this.gitType = gitType;
    }

    public String getGitBranch() {
        return gitBranch;
    }

    public void setGitBranch(String gitBranch) {
        this.gitBranch = gitBranch;
    }

    public String getGitRootDir() {
        return gitRootDir;
    }

    public void setGitRootDir(String gitRootDir) {
        this.gitRootDir = gitRootDir;
    }

    public List<DiffVariableRequest> getDiffVariableRequestList() {
        return diffVariableRequestList;
    }

    public void setDiffVariableRequestList(List<DiffVariableRequest> diffVariableRequestList) {
        this.diffVariableRequestList = diffVariableRequestList;
    }

    public static void checkRequest(DownloadProjectRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("Request {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (CollectionUtils.isEmpty(request.getProjectId())) {
            throw new UnExpectedRequestException("Project {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (request.getDownloadType() == null) {
            throw new UnExpectedRequestException("Download type {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (request.getDownloadType().equals(ProjectTransportTypeEnum.GIT.getCode())) {
            CommonChecker.checkString(request.getGitRepo(), "Git Repo");
            CommonChecker.checkString(request.getGitBranch(), "Git Branch");
            CommonChecker.checkString(request.getGitRootDir(), "Git Root Dir");
        }
    }

    public List<Long> getRuleIds() {
        return ruleIds;
    }

    public void setRuleIds(List<Long> ruleIds) {
        this.ruleIds = ruleIds;
    }

    public List<String> getRuleNames() {
        return ruleNames;
    }

    public void setRuleNames(List<String> ruleNames) {
        this.ruleNames = ruleNames;
    }

    public String getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(String operateUser) {
        this.operateUser = operateUser;
    }

}
