package com.webank.wedatasphere.qualitis.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Id;

/**
 * @author allenzhou@webank.com
 * @date 2021/10/5 11:10
 */
@Entity
@Table(name = "qualitis_upload_record")
public class UploadRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ims_rule_count")
    private Integer imsRuleCount;
    @Column(name = "upload_status")
    private Boolean status;
    @Column(name = "upload_date")
    private Date uploadDate;
    @Column(name = "upload_time")
    private String uploadTime;
    @Column(name = "upload_err_msg", columnDefinition = "TEXT")
    private String errMsg;

    public UploadRecord() {
    }

    public UploadRecord(Integer imsRuleCount, Boolean status, Date uploadDate, String uploadTime, String errMsg) {
        this.imsRuleCount = imsRuleCount;
        this.status = status;
        this.uploadDate = uploadDate;
        this.uploadTime = uploadTime;
        this.errMsg = errMsg;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getImsRuleCount() {
        return imsRuleCount;
    }

    public void setImsRuleCount(Integer imsRuleCount) {
        this.imsRuleCount = imsRuleCount;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
