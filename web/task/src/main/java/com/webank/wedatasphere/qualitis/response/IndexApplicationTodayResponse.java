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

package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author v_wblwyan
 * @date 2018-11-14
 */
public class IndexApplicationTodayResponse {

  @JsonProperty("total_num")
  private long totalNum;
  @JsonProperty("application_succ_num")
  private long applicationSuccNum;
  @JsonProperty("application_fail_num")
  private long applicationFailNum;
  @JsonProperty("application_fail_check_num")
  private long applicationFailCheckNum;
  @JsonProperty("application_total_num")
  private long applicationTotalNum;
  private List<IndexApplicationResponse> applications;

  public IndexApplicationTodayResponse() {
  }

  public IndexApplicationTodayResponse(List<IndexApplicationResponse> applicationResponses, long totalNum, long totalSuccNum, long totalFailNum, long totalFailCheckNum) {
    this.totalNum = totalNum;
    this.applications = applicationResponses;
    this.applicationTotalNum = totalNum;
    this.applicationSuccNum = totalSuccNum;
    this.applicationFailNum = totalFailNum;
    this.applicationFailCheckNum = totalFailCheckNum;
  }


  public long getApplicationSuccNum() {
    return applicationSuccNum;
  }

  public void setApplicationSuccNum(long applicationSuccNum) {
    this.applicationSuccNum = applicationSuccNum;
  }

  public long getApplicationFailNum() {
    return applicationFailNum;
  }

  public void setApplicationFailNum(long applicationFailNum) {
    this.applicationFailNum = applicationFailNum;
  }

  public long getApplicationTotalNum() {
    return applicationTotalNum;
  }

  public void setApplicationTotalNum(long applicationTotalNum) {
    this.applicationTotalNum = applicationTotalNum;
  }

  public long getTotalNum() {
    return totalNum;
  }

  public void setTotalNum(long totalNum) {
    this.totalNum = totalNum;
  }

  public List<IndexApplicationResponse> getApplications() {
    return applications;
  }

  public void setApplications(List<IndexApplicationResponse> applications) {
    this.applications = applications;
  }

  public long getApplicationFailCheckNum() {
    return applicationFailCheckNum;
  }

  public void setApplicationFailCheckNum(long applicationFailCheckNum) {
    this.applicationFailCheckNum = applicationFailCheckNum;
  }
}
