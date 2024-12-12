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

package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import org.apache.commons.lang.StringUtils;

/**
 * @author v_wblwyan
 * @date 2018-11-14
 */
public class IndexRequest {

  @JsonIgnore
  private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
  @JsonProperty("step_size")
  private Integer stepSize;
  @JsonProperty("start_date")
  private String startDate;
  @JsonProperty("end_date")
  private String endDate;
  private String user;

  public IndexRequest() {
    // Default Constructor
  }

  public static void checkRequest(IndexRequest request) throws UnExpectedRequestException {
    boolean useStepSize = request.getStepSize() != null;
    boolean useDate = StringUtils.isNotBlank(request.getStartDate()) || StringUtils.isNotBlank(request.getEndDate());
    if (useStepSize && useDate) {
      throw new UnExpectedRequestException("{&STEP_SIZE_AND_START_DATE_END_DATE_CAN_NOT_ALL_USE_AT_THE_SAME_TIME}");
    }
    if (request.getStepSize() == null) {
      com.webank.wedatasphere.qualitis.project.request.CommonChecker.checkString(request.getStartDate(), "start_date");
      com.webank.wedatasphere.qualitis.project.request.CommonChecker.checkString(request.getEndDate(), "end_date");
      com.webank.wedatasphere.qualitis.project.request.CommonChecker.checkDateStringPattern(request.getStartDate(), DATE_FORMAT_PATTERN,
                                           "start_date");
      com.webank.wedatasphere.qualitis.project.request.CommonChecker.checkDateStringPattern(request.getEndDate(), DATE_FORMAT_PATTERN, "end_date");
    }
    if (StringUtils.isBlank(request.getStartDate())) {
      CommonChecker.checkObject(request.getStepSize(), "step_size");
    }
  }

  public Integer getStepSize() {
    return stepSize;
  }

  public void setStepSize(Integer stepSize) {
    this.stepSize = stepSize;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }
}
