package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
//import com.webank.wedatasphere.qualitis.function.entity.LinkisUdf;
//import com.webank.wedatasphere.qualitis.function.entity.LinkisUdfEnableEngine;
//import com.webank.wedatasphere.qualitis.function.entity.LinkisUdfEnableCluster;
import com.webank.wedatasphere.qualitis.response.DepartmentSubInfoResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author allenzhou@webank.com
 * @date 2022/11/7 17:40
 */
public class UdfResponse {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("cn_name")
    private String cnName;
    @JsonProperty("desc")
    private String desc;
    @JsonProperty("dir")
    private String dir;

    @JsonProperty("file")
    private String file;
    @JsonProperty("enter")
    private String enter;
    @JsonProperty("return")
    private String returnType;
    @JsonProperty("register_name")
    private String registerName;
    @JsonProperty("impl_type")
    private Integer implType;

    @JsonProperty("enable_engine")
    private List<Integer> enableEngine;
    @JsonProperty("enable_cluster")
    private List<String> enableCluster;

    @JsonProperty("dev_department_name")
    private String devDepartmentName;
    @JsonProperty("ops_department_name")
    private String opsDepartmentName;
    @JsonProperty("dev_department_id")
    private Long devDepartmentId;
    @JsonProperty("ops_department_id")
    private Long opsDepartmentId;
    @JsonProperty("visibility_department_list")
    private List<DepartmentSubInfoResponse> visibilityDepartmentList;


    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("modify_user")
    private String modifyUser;
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("modify_time")
    private String modifyTime;

    @JsonProperty("status")
    private Boolean status;

    public UdfResponse(Long id) {
        this.id = id;
        enableEngine = new ArrayList<>();
        enableCluster = new ArrayList<>();
        visibilityDepartmentList = new ArrayList<>();
    }

//    public UdfResponse(LinkisUdf linkisUdf) {
//        visibilityDepartmentList = new ArrayList<>();
//        enableEngine = new ArrayList<>();
//        enableCluster = new ArrayList<>();
//
//        this.id = linkisUdf.getId();
//        this.name = linkisUdf.getName();
//        this.enter = linkisUdf.getEnter();
//        this.desc = linkisUdf.getUdfDesc();
//        this.cnName = linkisUdf.getCnName();
//        this.dir = linkisUdf.getDirectory();
//        this.file = linkisUdf.getUploadPath();
//        this.returnType = linkisUdf.getReturnType();
//        this.implType = linkisUdf.getImplTypeCode();
//        this.registerName = linkisUdf.getRegisterName();
//        enableEngine.addAll(linkisUdf.getLinkisUdfEnableEngineSet().stream().map(LinkisUdfEnableEngine::getEnableEngineCode).collect(Collectors.toList()));
//        enableCluster.addAll(linkisUdf.getLinkisUdfEnableClusterSet().stream().map(LinkisUdfEnableCluster::getEnableClusterName).collect(Collectors.toList()));
//        this.devDepartmentName = linkisUdf.getDevDepartmentName();
//        this.opsDepartmentName = linkisUdf.getOpsDepartmentName();
//        this.devDepartmentId = linkisUdf.getDevDepartmentId();
//        this.opsDepartmentId = linkisUdf.getOpsDepartmentId();
//        this.createUser = linkisUdf.getCreateUser();
//        this.modifyUser = linkisUdf.getModifyUser();
//        this.createTime = linkisUdf.getCreateTime();
//        this.modifyTime = linkisUdf.getModifyTime();
//        this.status = linkisUdf.getStatus();
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getEnter() {
        return enter;
    }

    public void setEnter(String enter) {
        this.enter = enter;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getRegisterName() {
        return registerName;
    }

    public void setRegisterName(String registerName) {
        this.registerName = registerName;
    }

    public Integer getImplType() {
        return implType;
    }

    public void setImplType(Integer implType) {
        this.implType = implType;
    }

    public List<Integer> getEnableEngine() {
        return enableEngine;
    }

    public void setEnableEngine(List<Integer> enableEngine) {
        this.enableEngine = enableEngine;
    }

    public List<String> getEnableCluster() {
        return enableCluster;
    }

    public void setEnableCluster(List<String> enableCluster) {
        this.enableCluster = enableCluster;
    }

    public String getDevDepartmentName() {
        return devDepartmentName;
    }

    public void setDevDepartmentName(String devDepartmentName) {
        this.devDepartmentName = devDepartmentName;
    }

    public String getOpsDepartmentName() {
        return opsDepartmentName;
    }

    public void setOpsDepartmentName(String opsDepartmentName) {
        this.opsDepartmentName = opsDepartmentName;
    }

    public Long getDevDepartmentId() {
        return devDepartmentId;
    }

    public void setDevDepartmentId(Long devDepartmentId) {
        this.devDepartmentId = devDepartmentId;
    }

    public Long getOpsDepartmentId() {
        return opsDepartmentId;
    }

    public void setOpsDepartmentId(Long opsDepartmentId) {
        this.opsDepartmentId = opsDepartmentId;
    }

    public List<DepartmentSubInfoResponse> getVisibilityDepartmentList() {
        return visibilityDepartmentList;
    }

    public void setVisibilityDepartmentList(List<DepartmentSubInfoResponse> visibilityDepartmentList) {
        this.visibilityDepartmentList = visibilityDepartmentList;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
