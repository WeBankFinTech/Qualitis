package com.webank.wedatasphere.qualitis.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author v_gaojiedeng@webank.com
 */
//@Entity
//@Table(name = "qualitis_mail_lock_record")
public class MailLockRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "send_mail_count")
    private Integer sendMailCount;
    @Column(name = "send_status")
    private Boolean status;
    @Column(name = "send_date")
    private Date sendDate;
    @Column(name = "send_time")
    private String sendTime;
    @Column(name = "send_err_msg", columnDefinition = "TEXT")
    private String errMsg;
    @Column(name = "execution_frequency")
    private Integer executionFrequency;

    public MailLockRecord() {
    }

    public MailLockRecord(Integer sendMailCount, Boolean status, Date sendDate, String sendTime, String errMsg, Integer executionFrequency) {
        this.sendMailCount = sendMailCount;
        this.status = status;
        this.sendDate = sendDate;
        this.sendTime = sendTime;
        this.errMsg = errMsg;
        this.executionFrequency = executionFrequency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSendMailCount() {
        return sendMailCount;
    }

    public void setSendMailCount(Integer sendMailCount) {
        this.sendMailCount = sendMailCount;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Integer getExecutionFrequency() {
        return executionFrequency;
    }

    public void setExecutionFrequency(Integer executionFrequency) {
        this.executionFrequency = executionFrequency;
    }
}
