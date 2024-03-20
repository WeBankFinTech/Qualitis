package com.webank.wedatasphere.qualitis.rule.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum TemplateFileTypeEnum {

    /**
     * -- 目录容量 目录文件数  分区数
     */
    DIRECTORY_CAPACITY(1, "目录容量"),
    NUMBER_CATALOG_FILES(2, "目录文件数"),
    NUMBER_PARTITIONS(3, "分区数");

    private Integer code;
    private String message;

    TemplateFileTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static List<Map<String, Object>> getTemplateFileTypeList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (TemplateFileTypeEnum templateFileTypeEnum : TemplateFileTypeEnum.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("code", templateFileTypeEnum.code);
            item.put("message", templateFileTypeEnum.message);
            list.add(item);

        }
        return list;
    }

    public static String getTemplateFileTypeByCode(Integer code) {
        for (TemplateFileTypeEnum e : TemplateFileTypeEnum.values()) {
            if (code.equals(e.getCode())) {
                return e.getMessage();
            }
        }
        return null;
    }

    public static Integer getCode(String message) {
        for (TemplateFileTypeEnum templateFileTypeEnum : TemplateFileTypeEnum.values()) {
            if (templateFileTypeEnum.getMessage().equals(message)) {
                return templateFileTypeEnum.getCode();
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
