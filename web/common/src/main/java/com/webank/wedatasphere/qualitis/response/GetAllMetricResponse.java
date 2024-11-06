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
 * @author v_wenxuanzhang
 */
public class GetAllMetricResponse<T> {

    private String title;
    private String dssUrl;
    private List<T> metrics;

    public GetAllMetricResponse() {
        // Default Constructor
    }

    public GetAllMetricResponse(String title, String dssUrl, List<T> metrics) {
        this.title = title;
        this.dssUrl = dssUrl;
        this.metrics = metrics;
    }

    public String getDssUrl() {
        return dssUrl;
    }

    public void setDssUrl(String dssUrl) {
        this.dssUrl = dssUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<T> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<T> metrics) {
        this.metrics = metrics;
    }

    @Override
    public String toString() {
        return "GetAllMetricResponse{" +
                "title=" + title +
                "dssUrl=" + dssUrl +
                ", metrics=" + metrics +
                '}';
    }
}
