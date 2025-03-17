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

package com.webank.wedatasphere.qualitis.function.entity;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import javax.persistence.Id;
import java.util.Set;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 * @author allenzhou
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_linkis_udf")
public class LinkisUdf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", length = 128)
    private String name;
    @Column(name = "cn_name", length = 128)
    private String cnName;
    @Column(name = "udf_desc")
    private String udfDesc;

    @Column(name = "dev_department_name")
    private String devDepartmentName;
    @Column(name = "ops_department_name")
    private String opsDepartmentName;
    @Column(name = "dev_department_id")
    private Long devDepartmentId;
    @Column(name = "ops_department_id")
    private Long opsDepartmentId;

    @Column(name = "enter")
    private String enter;
    @Column(name = "return_type")
    private String returnType;
    @Column(name = "register_name")
    private String registerName;
    @Column(name = "directory")
    private String directory;

    @Column(name = "upload_path")
    private String uploadPath;
    @Column(name = "status")
    private Boolean status;

    @Column(name = "impl_type_code")
    private Integer implTypeCode;
    @OneToMany(mappedBy = "linkisUdf", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private Set<LinkisUdfEnableEngine> linkisUdfEnableEngineSet;
    @OneToMany(mappedBy = "linkisUdf", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private Set<LinkisUdfEnableCluster> linkisUdfEnableClusterSet;

    @Column(name = "create_user", length = 50)
    private String createUser;
    @Column(name = "create_time", length = 25)
    private String createTime;
    @Column(name = "modify_user", length = 50)
    private String modifyUser;
    @Column(name = "modify_time", length = 25)
    private String modifyTime;

    public LinkisUdf() {
    }

    public LinkisUdf(String name, String cnName, String udfDesc, String enter, String returnType, String registerName, String directory, Integer implTypeCode, Long devDepartmentId
        , String devDepartmentName, Long opsDepartmentId, String opsDepartmentName, String uploadPath, Boolean status) {
        this.name = name;
        this.cnName = cnName;
        this.udfDesc = udfDesc;
        this.enter = enter;
        this.returnType = returnType;
        this.registerName = registerName;
        this.directory = directory;
        this.implTypeCode = implTypeCode;
        this.devDepartmentId = devDepartmentId;
        this.devDepartmentName = devDepartmentName;
        this.opsDepartmentId = opsDepartmentId;
        this.opsDepartmentName = opsDepartmentName;
        this.uploadPath = uploadPath;
        this.status = status;
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

    public String getUdfDesc() {
        return udfDesc;
    }

    public void setUdfDesc(String udfDesc) {
        this.udfDesc = udfDesc;
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

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public Integer getImplTypeCode() {
        return implTypeCode;
    }

    public void setImplTypeCode(Integer implTypeCode) {
        this.implTypeCode = implTypeCode;
    }

    public Set<LinkisUdfEnableEngine> getLinkisUdfEnableEngineSet() {
        return linkisUdfEnableEngineSet;
    }

    public void setLinkisUdfEnableEngineSet(Set<LinkisUdfEnableEngine> linkisUdfEnableEngineSet) {
        this.linkisUdfEnableEngineSet = linkisUdfEnableEngineSet;
    }

    public Set<LinkisUdfEnableCluster> getLinkisUdfEnableClusterSet() {
        return linkisUdfEnableClusterSet;
    }

    public void setLinkisUdfEnableClusterSet(Set<LinkisUdfEnableCluster> linkisUdfEnableClusterSet) {
        this.linkisUdfEnableClusterSet = linkisUdfEnableClusterSet;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "LinkisUdf{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", cnName='" + cnName + '\'' +
            ", createUser='" + createUser + '\'' +
            ", createTime='" + createTime + '\'' +
            ", modifyUser='" + modifyUser + '\'' +
            ", modifyTime='" + modifyTime + '\'' +
            '}';
    }
}
