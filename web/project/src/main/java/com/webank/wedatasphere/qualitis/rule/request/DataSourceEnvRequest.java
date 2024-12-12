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

package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSourceEnv;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceEnv;

/**
 * @author allenzhou
 */
public class DataSourceEnvRequest {
    @JsonProperty("env_id")
    private Long envId;
    @JsonProperty("env_name")
    private String envName;
    @JsonProperty("dcn_num")
    private String dcnNum;
    @JsonProperty("logic_area")
    private String logicArea;
    
    public DataSourceEnvRequest() {
        // Default Constructor
    }

    public DataSourceEnvRequest(RuleDataSourceEnv env) {
        this.envId = env.getEnvId();
        this.envName = env.getEnvName();
    }

    public DataSourceEnvRequest(LinkisDataSourceEnv linkisDataSourceEnv) {
        this.envId = linkisDataSourceEnv.getEnvId();
        this.envName = linkisDataSourceEnv.getEnvName();
        this.dcnNum = linkisDataSourceEnv.getDcnNum();
        this.logicArea = linkisDataSourceEnv.getLogicArea();
        if (this.envName != null) {
            this.envName = this.envName.replace(linkisDataSourceEnv.getLinkisDataSourceId() + SpecCharEnum.BOTTOM_BAR.getValue(), "");
        }
    }

    public String getDcnNum() {
        return dcnNum;
    }

    public void setDcnNum(String dcnNum) {
        this.dcnNum = dcnNum;
    }

    public String getLogicArea() {
        return logicArea;
    }

    public void setLogicArea(String logicArea) {
        this.logicArea = logicArea;
    }

    public Long getEnvId() {
        return envId;
    }

    public void setEnvId(Long envId) {
        this.envId = envId;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }
    
}
