package com.webank.wedatasphere.qualitis.response;


import java.math.BigDecimal;
import java.util.List;

public class LineDataList {

    private List<Long> timestampList;
    private List<BigDecimal> valueList;

    public List<Long> getTimestampList() {
        return timestampList;
    }

    public void setTimestampList(List<Long> timestampList) {
        this.timestampList = timestampList;
    }

    public List<BigDecimal> getValueList() {
        return valueList;
    }

    public void setValueList(List<BigDecimal> valueList) {
        this.valueList = valueList;
    }
}
