package com.webank.wedatasphere.qualitis.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public class EmailEntity {

    /**
     * 发件人邮箱
     */
    @JsonProperty("From")
    private String from;

    /**
     * 收件人邮箱，多人用分号隔开。
     */
    @JsonProperty("To")
    private String to;

    @JsonProperty("ToList")
    private List<String> toList;

    /**
     * 邮件标题
     */
    @JsonProperty("Title")
    private String title;
    /**
     * 邮件内容,如果带图片的话，要配合下面的Attachments参数使用。图片： <img src='cid:下面的ContentId' />
     */
    @JsonProperty("Content")
    private String content;

    /**
     * 邮件格式，0 文本、1 Html。默认值为0。
     */
    @JsonProperty("BodyFormat")
    private Integer bodyFormat;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List<String> getToList() {
        return toList;
    }

    public void setToList(List<String> toList) {
        this.toList = toList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getBodyFormat() {
        return bodyFormat;
    }

    public void setBodyFormat(Integer bodyFormat) {
        this.bodyFormat = bodyFormat;
    }
}
