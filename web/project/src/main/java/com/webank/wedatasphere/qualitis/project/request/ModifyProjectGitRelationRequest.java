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
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

/**
 * @author howeye
 */
public class ModifyProjectGitRelationRequest {
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("git_repo")
    private String gitRepo;
    @JsonProperty("git_type")
    private Integer gitType;
    @JsonProperty("git_branch")
    private String gitBranch;
    @JsonProperty("git_root_dir")
    private String gitRootDir;

    public ModifyProjectGitRelationRequest() {
        // Default Constructor
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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

    public static void checkRequest(ModifyProjectGitRelationRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request Object");
        CommonChecker.checkObject(request.getProjectId(), "Project ID");

        CommonChecker.checkObject(request.getGitType(), "Git type");
        CommonChecker.checkString(request.getGitRepo(), "Git repo url");
    }
}
