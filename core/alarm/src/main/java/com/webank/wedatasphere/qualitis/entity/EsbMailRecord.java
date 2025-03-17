package com.webank.wedatasphere.qualitis.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author v_gaojiedeng@webank.com
 */
//@Entity
//@Table(name = "qualitis_esb_mail_record")
public class EsbMailRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private String code;

    @Column(name = "response_message", columnDefinition = "TEXT")
    private String responseMessage;
    @Column(name = "assembly_result", columnDefinition = "TEXT")
    private String assemblyResult;
    @Column(name = "create_user", length = 50)
    private String createUser;
    @Column(name = "create_time", length = 25)
    private String createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getAssemblyResult() {
        return assemblyResult;
    }

    public void setAssemblyResult(String assemblyResult) {
        this.assemblyResult = assemblyResult;
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

    @Override
    public String toString() {
        return "EsbMailRecord{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", code='" + code + '\'' +
                ", responseMessage='" + responseMessage + '\'' +
                ", assemblyResult='" + assemblyResult + '\'' +
                ", createUser='" + createUser + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
