package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author v_gaojiedeng@webank.com
 */
public class DmsQueryRequest {

    @JsonProperty("std_sub_name")
    private String stdSubName;

    @JsonProperty("std_big_category_name")
    private String stdBigCategoryName;

    @JsonProperty("small_category_name")
    private String smallCategoryName;

    @JsonProperty("std_small_category_urn")
    private String stdSmallCategoryUrn;

    @JsonProperty("std_cn_name")
    private String stdCnName;

    @JsonProperty("std_urn")
    private String stdUrn;

    private String stdCode;

    private int page;
    private int size;

    public DmsQueryRequest() {
        this.page = 0;
        this.size = 10;
    }

    public String getStdSmallCategoryUrn() {
        return stdSmallCategoryUrn;
    }

    public void setStdSmallCategoryUrn(String stdSmallCategoryUrn) {
        this.stdSmallCategoryUrn = stdSmallCategoryUrn;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getStdUrn() {
        return stdUrn;
    }

    public void setStdUrn(String stdUrn) {
        this.stdUrn = stdUrn;
    }

    public String getStdSubName() {
        return stdSubName;
    }

    public void setStdSubName(String stdSubName) {
        this.stdSubName = stdSubName;
    }

    public String getStdBigCategoryName() {
        return stdBigCategoryName;
    }

    public void setStdBigCategoryName(String stdBigCategoryName) {
        this.stdBigCategoryName = stdBigCategoryName;
    }

    public String getSmallCategoryName() {
        return smallCategoryName;
    }

    public void setSmallCategoryName(String smallCategoryName) {
        this.smallCategoryName = smallCategoryName;
    }

    public String getStdCnName() {
        return stdCnName;
    }

    public void setStdCnName(String stdCnName) {
        this.stdCnName = stdCnName;
    }

    public String getStdCode() {
        return stdCode;
    }

    public void setStdCode(String stdCode) {
        this.stdCode = stdCode;
    }
}
