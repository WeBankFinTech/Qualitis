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
import com.webank.wedatasphere.qualitis.constant.AutoCollectStatusEnum;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.ImsMetricAutoCollectRecord;

import java.util.List;

/**
 * @author v_wblwyan
 * @date 2018-11-14
 */
public class IndexAutoCollectRecordChartResponse {
  private String date;
  @JsonProperty("no_permission_num")
  private int noPermissionNum;
  @JsonProperty("analysised_num")
  private int analysisedNum;
  @JsonProperty("collected_num")
  private int collectedNum;
  @JsonProperty("run_date_scheduler_num")
  private int runDateSchedulterNum;
  @JsonProperty("history_scheduler_num")
  private int historySchedulterNum;
  @JsonProperty("total_num")
  private int totalNum;

  public IndexAutoCollectRecordChartResponse() {
  }

  public IndexAutoCollectRecordChartResponse(String date) {
    this.date = date;
  }

  public IndexAutoCollectRecordChartResponse(String date, List<ImsMetricAutoCollectRecord> datas) {
    this.date = date;
    this.totalNum = datas.size();
    for (ImsMetricAutoCollectRecord imsMetricAutoCollectRecord : datas) {
      if (imsMetricAutoCollectRecord.getStatus().equals(AutoCollectStatusEnum.NO_PERMISSION.getCode())) {
        this.noPermissionNum++;
      }
      if (imsMetricAutoCollectRecord.getStatus().equals(AutoCollectStatusEnum.ANALYSISED.getCode())) {
        this.analysisedNum++;
      }
      if (imsMetricAutoCollectRecord.getStatus().equals(AutoCollectStatusEnum.COLLECTED.getCode())) {
        this.collectedNum++;
      }
      if (imsMetricAutoCollectRecord.getStatus().equals(AutoCollectStatusEnum.RUNDATE_SCHEDULER.getCode())) {
        this.runDateSchedulterNum++;
      }
      if (imsMetricAutoCollectRecord.getStatus().equals(AutoCollectStatusEnum.HISTORY_SCHEDULER.getCode())) {
        this.historySchedulterNum++;
      }
    }
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public int getNoPermissionNum() {
    return noPermissionNum;
  }

  public void setNoPermissionNum(int noPermissionNum) {
    this.noPermissionNum = noPermissionNum;
  }

  public int getAnalysisedNum() {
    return analysisedNum;
  }

  public void setAnalysisedNum(int analysisedNum) {
    this.analysisedNum = analysisedNum;
  }

  public int getCollectedNum() {
    return collectedNum;
  }

  public void setCollectedNum(int collectedNum) {
    this.collectedNum = collectedNum;
  }

  public int getRunDateSchedulterNum() {
    return runDateSchedulterNum;
  }

  public void setRunDateSchedulterNum(int runDateSchedulterNum) {
    this.runDateSchedulterNum = runDateSchedulterNum;
  }

  public int getHistorySchedulterNum() {
    return historySchedulterNum;
  }

  public void setHistorySchedulterNum(int historySchedulterNum) {
    this.historySchedulterNum = historySchedulterNum;
  }

  public int getTotalNum() {
    return totalNum;
  }

  public void setTotalNum(int totalNum) {
    this.totalNum = totalNum;
  }
}
