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
import com.webank.wedatasphere.qualitis.constant.ApplicationStatusEnum;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.constant.ApplicationStatusEnum;
import com.webank.wedatasphere.qualitis.entity.Application;

import java.util.List;

/**
 * @author v_wblwyan
 * @date 2018-11-14
 */
public class IndexApplicationChartResponse {
  private String date;
  @JsonProperty("application_succ_num")
  private int applicationSuccNum;
  @JsonProperty("application_fail_num")
  private int applicationFailNum;
  @JsonProperty("application_fail_check_num")
  private long applicationFailCheckNum;
  @JsonProperty("application_total_num")
  private int applicationTotalNum;

  public IndexApplicationChartResponse() {
  }

  public IndexApplicationChartResponse(String date) {
    this.date = date;
  }

  public IndexApplicationChartResponse(String date, List<Application> datas) {
    this.date = date;
    this.applicationTotalNum = datas.size();
    for (Application application : datas) {
      if (application.getStatus().equals(ApplicationStatusEnum.FINISHED.getCode())) {
        this.applicationSuccNum++;
      }
      if (application.getStatus().equals(ApplicationStatusEnum.FAILED.getCode())) {
        this.applicationFailNum++;
      }
      if (application.getStatus().equals(ApplicationStatusEnum.NOT_PASS.getCode())) {
        this.applicationFailCheckNum++;
      }
    }
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public int getApplicationSuccNum() {
    return applicationSuccNum;
  }

  public void setApplicationSuccNum(int applicationSuccNum) {
    this.applicationSuccNum = applicationSuccNum;
  }

  public int getApplicationFailNum() {
    return applicationFailNum;
  }

  public void setApplicationFailNum(int applicationFailNum) {
    this.applicationFailNum = applicationFailNum;
  }

  public int getApplicationTotalNum() {
    return applicationTotalNum;
  }

  public void setApplicationTotalNum(int applicationTotalNum) {
    this.applicationTotalNum = applicationTotalNum;
  }

  public long getApplicationFailCheckNum() {
    return applicationFailCheckNum;
  }

  public void setApplicationFailCheckNum(long applicationFailCheckNum) {
    this.applicationFailCheckNum = applicationFailCheckNum;
  }
}
