package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2022/11/7 17:40
 */
public class UdfRequest {
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
    private List<DepartmentSubInfoRequest> visibilityDepartmentList;

    @JsonProperty("status")
    private Boolean status;

    private int page;
    private int size;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("modify_user")
    private String modifyUser;

    public static void checkRequestForAdd(UdfRequest request) throws UnExpectedRequestException {
        CommonChecker.checkString(request.getName(), "Name");
        CommonChecker.checkString(request.getCnName(), "Chinese Name");
        CommonChecker.checkString(request.getDesc(), "Description");
        CommonChecker.checkString(request.getDir(), "Dir");

        CommonChecker.checkString(request.getFile(), "File");
        CommonChecker.checkString(request.getEnter(), "Enter");
        CommonChecker.checkString(request.getReturnType(), "Return Type");
        CommonChecker.checkString(request.getRegisterName(), "Register Name");
        CommonChecker.checkObject(request.getImplType(), "Impl Type");

        CommonChecker.checkCollections(request.getEnableEngine(), "Enable Engine");
        CommonChecker.checkCollections(request.getEnableCluster(), "Enable Cluster");

        CommonChecker.checkString(request.getDevDepartmentName(), "Dev Department");
        CommonChecker.checkString(request.getOpsDepartmentName(), "Ops Department");

        CommonChecker.checkObject(request.getStatus(), "Status");
    }

    public static void checkRequestForModify(UdfRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request.getId(), "ID");
        checkRequestForAdd(request);
    }

    public static void checkRequestForGetDetail(Long request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "ID");
    }

    public static void checkRequestForGetAllWithPage(UdfRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request.getPage(), "Page");
        CommonChecker.checkObject(request.getSize(), "Size");
    }

    public static void checkRequestForDelete(UdfRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request.getId(), "ID");
    }

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

    public List<DepartmentSubInfoRequest> getVisibilityDepartmentList() {
        return visibilityDepartmentList;
    }

    public void setVisibilityDepartmentList(List<DepartmentSubInfoRequest> visibilityDepartmentList) {
        this.visibilityDepartmentList = visibilityDepartmentList;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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

    @Override
    public String toString() {
        return "UdfRequest{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", cnName='" + cnName + '\'' +
            ", desc='" + desc + '\'' +
            ", dir='" + dir + '\'' +
            ", file='" + file + '\'' +
            ", enter='" + enter + '\'' +
            ", returnType='" + returnType + '\'' +
            ", registerName='" + registerName + '\'' +
            ", implType=" + implType +
            ", enableEngine=" + enableEngine +
            ", enableCluster=" + enableCluster +
            ", devDepartmentName='" + devDepartmentName + '\'' +
            ", opsDepartmentName='" + opsDepartmentName + '\'' +
            ", devDepartmentId=" + devDepartmentId +
            ", opsDepartmentId=" + opsDepartmentId +
            ", visibilityDepartmentList=" + visibilityDepartmentList +
            ", status=" + status +
            '}';
    }
}
