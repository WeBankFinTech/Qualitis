package com.webank.wedatasphere.qualitis.project.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2023/4/21 9:40
 */
public class UploadProjectRequest {
    @JsonProperty("project_id")
    private Long projectId;

    @JsonProperty("upload_type")
    private Integer uploadType;

    @JsonProperty("git_repo")
    private String gitRepo;
    @JsonProperty("git_type")
    private Integer gitType;
    @JsonProperty("git_branch")
    private String gitBranch;
    @JsonProperty("git_root_dir")
    private String gitRootDir;

    @JsonProperty("zip_path")
    private String zipPath;

    @JsonProperty("diff_variables")
    private List<DiffVariableRequest> diffVariableRequestList;

    @JsonProperty("login_user")
    private String loginUser;
    @JsonProperty("operate_user")
    private String operateUser;

    @JsonProperty("increment")
    private Boolean increment;

    public UploadProjectRequest() {
        // Default Constructor
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getUploadType() {
        return uploadType;
    }

    public void setUploadType(Integer uploadType) {
        this.uploadType = uploadType;
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

    public String getZipPath() {
        return zipPath;
    }

    public void setZipPath(String zipPath) {
        this.zipPath = zipPath;
    }

    public List<DiffVariableRequest> getDiffVariableRequestList() {
        return diffVariableRequestList;
    }

    public void setDiffVariableRequestList(List<DiffVariableRequest> diffVariableRequestList) {
        this.diffVariableRequestList = diffVariableRequestList;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public String getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(String operateUser) {
        this.operateUser = operateUser;
    }

    public Boolean getIncrement() {
        return increment;
    }

    public void setIncrement(Boolean increment) {
        this.increment = increment;
    }
}
