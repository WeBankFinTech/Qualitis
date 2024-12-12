package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

/**
 * @author allenzhou
 */
public class TemplateStatisticsInputMetaRequest {
    @JsonProperty("input_meta_name")
    private String name;

    @JsonProperty("func_name")
    private String funcName;
    @JsonProperty("func_value")
    private String value;
    @JsonProperty("value_type")
    private Integer valueType;

    @JsonProperty("result_type")
    private String resultType;

    public TemplateStatisticsInputMetaRequest() {
        this.resultType = "Long";
    }

    public TemplateStatisticsInputMetaRequest(String name, String funcName, String value) {
        this.name = name;
        this.funcName = funcName;
        this.value = value;
    }

    public static void checkRequest(TemplateStatisticsInputMetaRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkString(request.getName(), "templateStatisticsInputMetaRequestName");
        CommonChecker.checkString(request.getFuncName(), "templateStatisticsInputMetaRequestFuncName");
        CommonChecker.checkString(request.getValue(), "templateStatisticsInputMetaRequestValue");
        CommonChecker.checkString(request.getFuncName(), "templateStatisticsInputMetaRequestFuncName");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getValueType() {
        return valueType;
    }

    public void setValueType(Integer valueType) {
        this.valueType = valueType;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }
}
