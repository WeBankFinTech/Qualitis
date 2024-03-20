package com.webank.wedatasphere.qualitis.metadata.response;

/**
 * @author v_minminghe@webank.com
 * @date 2022-06-01 15:19
 * @description
 */
public class DataMapResultInfo<T> {

    private String code;
    private String msg;
    private T data;

    public DataMapResultInfo() {

    }

    public DataMapResultInfo(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
