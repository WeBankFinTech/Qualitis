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
import com.webank.wedatasphere.qualitis.entity.FieldsAnalyse;

import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public class FieldsAnalyseResultResponse {


    @JsonProperty("fieldsAnalyseList")
    private List< FieldsAnalyse > fieldsAnalyseList;

    public FieldsAnalyseResultResponse() {
    }

    public FieldsAnalyseResultResponse(List< FieldsAnalyse > fieldsAnalyseList) {
        this.fieldsAnalyseList = fieldsAnalyseList;
    }

    public List< FieldsAnalyse > getFieldsAnalyseList() {
        return fieldsAnalyseList;
    }

    public void setFieldsAnalyseList(List< FieldsAnalyse > fieldsAnalyseList) {
        this.fieldsAnalyseList = fieldsAnalyseList;
    }
}
