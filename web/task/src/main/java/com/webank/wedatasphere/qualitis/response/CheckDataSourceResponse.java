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

import java.util.List;

/**
 * @author howeye
 */
public class CheckDataSourceResponse {

    private List<CheckSingleDataSourceResponse> single;
    private List<CheckCustomDataSourceResponse> custom;
    private List<CheckMultipleDataSourceResponse> multiple;
    private List<CheckFileDataSourceResponse> file;

    public CheckDataSourceResponse() {
    }

    public List<CheckSingleDataSourceResponse> getSingle() {
        return single;
    }

    public void setSingle(List<CheckSingleDataSourceResponse> single) {
        this.single = single;
    }

    public List<CheckCustomDataSourceResponse> getCustom() {
        return custom;
    }

    public void setCustom(List<CheckCustomDataSourceResponse> custom) {
        this.custom = custom;
    }

    public List<CheckMultipleDataSourceResponse> getMultiple() {
        return multiple;
    }

    public void setMultiple(List<CheckMultipleDataSourceResponse> multiple) {
        this.multiple = multiple;
    }

    public List<CheckFileDataSourceResponse> getFile() {
        return file;
    }

    public void setFile(List<CheckFileDataSourceResponse> file) {
        this.file = file;
    }
}
