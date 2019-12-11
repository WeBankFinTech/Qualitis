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

package com.webank.wedatasphere.qualitis.entity;

import com.webank.wedatasphere.qualitis.constant.ApplicationStatusEnum;
import com.webank.wedatasphere.qualitis.constant.ApplicationStatusEnum;

import javax.persistence.*;

/**
 * @author howeye
 */
@Entity
@Table(name = "qualitis_application")
public class Application {

    @Id
    @Column(name = "id", length = 40)
    private String id;

    @Column(name = "submit_time", length = 25)
    private String submitTime;
    @Column(name = "finish_time", length = 25)
    private String finishTime;
    @Column(name = "invoke_type")
    private Integer invokeType;

    @Column(name = "finish_task_num")
    private Integer finishTaskNum;
    @Column(name = "fail_task_num")
    private Integer failTaskNum;
    @Column(name = "not_pass_task_num")
    private Integer notPassTaskNum;
    @Column(name = "abnormal_task_num")
    private Integer abnormalTaskNum;
    @Column(name = "total_task_num")
    private Integer totalTaskNum;

    @Column(name = "execute_user", length = 150)
    private String executeUser;
    @Column(name = "create_user", length = 150)
    private String createUser;
    private Integer status;
    @Column(name = "rule_size")
    private Integer ruleSize;

    @Column(name = "exception_message", length = 10000)
    private String exceptionMessage;

    /**
     * Using saved db to save data that not pass verification
     */
    @Column(name = "saved_db", length = 100)
    private String savedDb;

    public Application() {
        this.finishTaskNum = 0;
        this.failTaskNum = 0;
        this.notPassTaskNum = 0;
        this.abnormalTaskNum = 0;
        this.status = ApplicationStatusEnum.SUCCESSFUL_CREATE_APPLICATION.getCode();
    }

    public void addSuccessJobNum() {
        this.finishTaskNum++;
    }

    public void addFailJobNum() {
        this.failTaskNum++;
    }

    public void addNotPassTaskNum() {
        this.notPassTaskNum++;
    }

    public void addAbnormalTaskNum() {
        this.abnormalTaskNum++;
    }

    public void resetTask() {
        this.finishTaskNum = 0;
        this.failTaskNum = 0;
        this.notPassTaskNum = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getInvokeType() {
        return invokeType;
    }

    public void setInvokeType(Integer invokeType) {
        this.invokeType = invokeType;
    }

    public Integer getFinishTaskNum() {
        return finishTaskNum;
    }

    public void setFinishTaskNum(Integer finishTaskNum) {
        this.finishTaskNum = finishTaskNum;
    }

    public Integer getFailTaskNum() {
        return failTaskNum;
    }

    public void setFailTaskNum(Integer failTaskNum) {
        this.failTaskNum = failTaskNum;
    }

    public Integer getAbnormalTaskNum() {
        return abnormalTaskNum;
    }

    public void setAbnormalTaskNum(Integer abnormalTaskNum) {
        this.abnormalTaskNum = abnormalTaskNum;
    }

    public Integer getTotalTaskNum() {
        return totalTaskNum;
    }

    public void setTotalTaskNum(Integer totalTaskNum) {
        this.totalTaskNum = totalTaskNum;
    }

    public String getExecuteUser() {
        return executeUser;
    }

    public void setExecuteUser(String executeUser) {
        this.executeUser = executeUser;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRuleSize() {
        return ruleSize;
    }

    public void setRuleSize(Integer ruleSize) {
        this.ruleSize = ruleSize;
    }

    public Integer getNotPassTaskNum() {
        return notPassTaskNum;
    }

    public void setNotPassTaskNum(Integer notPassTaskNum) {
        this.notPassTaskNum = notPassTaskNum;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getSavedDb() {
        return savedDb;
    }

    public void setSavedDb(String savedDb) {
        this.savedDb = savedDb;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    @Override
    public String toString() {
        return "Application{" +
                "id='" + id + '\'' +
                ", submitTime='" + submitTime + '\'' +
                ", finishTime='" + finishTime + '\'' +
                ", invokeType=" + invokeType +
                ", finishTaskNum=" + finishTaskNum +
                ", failTaskNum=" + failTaskNum +
                ", notPassTaskNum=" + notPassTaskNum +
                ", abnormalTaskNum=" + abnormalTaskNum +
                ", totalTaskNum=" + totalTaskNum +
                ", executeUser='" + executeUser + '\'' +
                ", createUser='" + createUser + '\'' +
                ", status=" + status +
                ", ruleSize=" + ruleSize +
                ", exceptionMessage='" + exceptionMessage + '\'' +
                ", savedDb='" + savedDb + '\'' +
                '}';
    }
}
