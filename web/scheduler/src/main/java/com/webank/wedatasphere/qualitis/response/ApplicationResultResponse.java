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

/**
 * @author howeye
 */
public class ApplicationResultResponse {

    @JsonProperty("pass_num")
    private Integer passNum;
    @JsonProperty("failed_num")
    private Integer failedNum;
    @JsonProperty("not_pass_num")
    private Integer notPassNum;
    @JsonProperty("result_message")
    private String resultMessage;

    public ApplicationResultResponse() {
    }

    public ApplicationResultResponse(Integer passNum, Integer failedNum, Integer notPassNum, String resultMessage) {
        this.passNum = passNum;
        this.failedNum = failedNum;
        this.notPassNum = notPassNum;
        this.resultMessage = resultMessage;
    }

    public Integer getPassNum() {
        return passNum;
    }

    public void setPassNum(Integer passNum) {
        this.passNum = passNum;
    }

    public Integer getFailedNum() {
        return failedNum;
    }

    public void setFailedNum(Integer failedNum) {
        this.failedNum = failedNum;
    }

    public Integer getNotPassNum() {
        return notPassNum;
    }

    public void setNotPassNum(Integer notPassNum) {
        this.notPassNum = notPassNum;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
}
