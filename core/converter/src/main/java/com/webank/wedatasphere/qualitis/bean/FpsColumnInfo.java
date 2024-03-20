package com.webank.wedatasphere.qualitis.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author allenzhou
 */
public class FpsColumnInfo {
    @JsonProperty("name")
    private String name;
    @JsonProperty("index")
    private Integer index;
    @JsonProperty("comment")
    private String comment;
    @JsonProperty("type")
    private String type;
    @JsonProperty("dateFormat")
    private String dateFormat;

    public FpsColumnInfo() {
    }

    public FpsColumnInfo(String name, Integer index, String comment, String type, String dateFormat) {
        this.name = name;
        this.index = index;
        this.comment = comment;
        this.type = type;
        this.dateFormat = dateFormat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
