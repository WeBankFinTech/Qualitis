package com.webank.wedatasphere.qualitis.response;

import java.io.Serializable;

/**
 * @author v_minminghe@webank.com
 * @date 2024-03-22 14:12
 * @description
 */
public class RetResponse<T> implements Serializable {

    private int retCode;
    private String retDetail;
    private T data;

    public RetResponse(int retCode, String retDetail, T data) {
        this.retCode = retCode;
        this.retDetail = retDetail;
        this.data = data;
    }

    public RetResponse(T data) {
        this.retCode = 0;
        this.retDetail = "success";
        this.data = data;
    }

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public String getRetDetail() {
        return retDetail;
    }

    public void setRetDetail(String retDetail) {
        this.retDetail = retDetail;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
