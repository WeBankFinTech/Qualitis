package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author v_minminghe@webank.com
 * @date 2024-04-17 17:38
 * @description
 */
public class ImsmetricViewDetailResponse {

    @JsonProperty("line_data_list")
    private LineDataList lineDataList;

    public LineDataList getLineDataList() {
        return lineDataList;
    }

    public void setLineDataList(LineDataList lineDataList) {
        this.lineDataList = lineDataList;
    }
}
