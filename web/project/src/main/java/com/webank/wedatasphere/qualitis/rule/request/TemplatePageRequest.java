package com.webank.wedatasphere.qualitis.rule.request;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateDataSourceTypeEnum;

/**
 * @author allenzhou@webank.com
 * @date 2021/11/4 14:45
 */
public class TemplatePageRequest {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 5;
    private static final String DEFAULT_DATASOURCE_TYPE = TemplateDataSourceTypeEnum.HIVE.getMessage();

    private int page;
    private int size;
    private String dataSourceType;

    public TemplatePageRequest() {
        page = DEFAULT_PAGE;
        size = DEFAULT_SIZE;
        dataSourceType = DEFAULT_DATASOURCE_TYPE;
    }

    public TemplatePageRequest(int page, int size, String dataSourceType) {
        this.page = page;
        this.size = size;
        this.dataSourceType = dataSourceType;
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

    public String getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public static void checkRequest(TemplatePageRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        long page = request.getPage();
        long size = request.getSize();
        if (page < 0) {
            throw new UnExpectedRequestException("page should >= 0, request: " + request);
        }
        if (size <= 0) {
            throw new UnExpectedRequestException("size should > 0, request: " + request);
        }
    }
}
