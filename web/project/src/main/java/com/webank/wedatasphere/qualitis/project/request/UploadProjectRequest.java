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

    @JsonProperty("zip_path")
    private String zipPath;

    @JsonProperty("diff_variables")
    private List<DiffVariableRequest> diffVariableRequestList;

    @JsonProperty("login_user")
    private String loginUser;

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
}
