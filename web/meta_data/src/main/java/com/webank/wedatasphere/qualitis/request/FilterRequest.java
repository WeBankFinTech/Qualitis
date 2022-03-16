package com.webank.wedatasphere.qualitis.request;

import static com.webank.wedatasphere.qualitis.project.request.CommonChecker.checkString;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import java.util.List;

/**
 * @author allenzhou
 */
public class FilterRequest {
    @JsonProperty("source_table")
    private String sourceTable;
    @JsonProperty("target_table")
    private String targetTable;
    @JsonProperty("source_filter")
    private String sourceFilter;
    @JsonProperty("target_filter")
    private String targetFilter;

    @JsonProperty("filter_column_list")
    private List<String> filterColumnList;

    public FilterRequest() {
    }

    public String getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public String getSourceFilter() {
        return sourceFilter;
    }

    public void setSourceFilter(String sourceFilter) {
        this.sourceFilter = sourceFilter;
    }

    public String getTargetFilter() {
        return targetFilter;
    }

    public void setTargetFilter(String targetFilter) {
        this.targetFilter = targetFilter;
    }

    public List<String> getFilterColumnList() {
        return filterColumnList;
    }

    public void setFilterColumnList(List<String> filterColumnList) {
        this.filterColumnList = filterColumnList;
    }

    public static void checkRequst(FilterRequest request) throws UnExpectedRequestException {
        checkString(request.getSourceTable(), "source db name");
        checkString(request.getTargetTable(), "target db name");
    }
}
