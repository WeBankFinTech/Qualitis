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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author howeye
 */
@Entity
@Table(name = "qualitis_gateway_job_info")
public class GatewayJobInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "job_id")
    private String jobId;
    @Column(name = "application_num")
    private Integer applicationNum;
    @Column(name = "application_query_timeout")
    private Integer applicationQueryTimeOut;

    public GatewayJobInfo() {
    }

    public GatewayJobInfo(String jobId, int applicationNum) {
        this.jobId = jobId;
        this.applicationNum = applicationNum;
    }

    public GatewayJobInfo(String jobId, int applicationNum, Integer gatewayQueryTimeout) {
        this.jobId = jobId;
        this.applicationNum = applicationNum;
        applicationQueryTimeOut = gatewayQueryTimeout;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getApplicationNum() {
        return applicationNum;
    }

    public void setApplicationNum(Integer applicationNum) {
        this.applicationNum = applicationNum;
    }

    public Integer getApplicationQueryTimeOut() {
        return applicationQueryTimeOut;
    }

    public void setApplicationQueryTimeOut(Integer applicationQueryTimeOut) {
        this.applicationQueryTimeOut = applicationQueryTimeOut;
    }
}
