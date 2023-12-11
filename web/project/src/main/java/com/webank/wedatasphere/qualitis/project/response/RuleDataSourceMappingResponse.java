package com.webank.wedatasphere.qualitis.project.response;

import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceMapping;
import org.springframework.beans.BeanUtils;

/**
 * @author v_minminghe@webank.com
 * @date 2023-04-25 15:45
 * @description
 */
public class RuleDataSourceMappingResponse {

    private String leftStatement;
    private Integer operation;
    private String rightStatement;
    private String leftColumnNames;
    private String rightColumnNames;
    private String leftColumnTypes;
    private String rightColumnTypes;
    private Integer mappingType;
    private String ruleName;

    public RuleDataSourceMappingResponse(){}

    public RuleDataSourceMappingResponse(RuleDataSourceMapping ruleDataSourceMapping) {
        BeanUtils.copyProperties(ruleDataSourceMapping, this);
        this.ruleName = ruleDataSourceMapping.getRule().getName();
    }

    public String getLeftStatement() {
        return leftStatement;
    }

    public void setLeftStatement(String leftStatement) {
        this.leftStatement = leftStatement;
    }

    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    public String getRightStatement() {
        return rightStatement;
    }

    public void setRightStatement(String rightStatement) {
        this.rightStatement = rightStatement;
    }

    public String getLeftColumnNames() {
        return leftColumnNames;
    }

    public void setLeftColumnNames(String leftColumnNames) {
        this.leftColumnNames = leftColumnNames;
    }

    public String getRightColumnNames() {
        return rightColumnNames;
    }

    public void setRightColumnNames(String rightColumnNames) {
        this.rightColumnNames = rightColumnNames;
    }

    public String getLeftColumnTypes() {
        return leftColumnTypes;
    }

    public void setLeftColumnTypes(String leftColumnTypes) {
        this.leftColumnTypes = leftColumnTypes;
    }

    public String getRightColumnTypes() {
        return rightColumnTypes;
    }

    public void setRightColumnTypes(String rightColumnTypes) {
        this.rightColumnTypes = rightColumnTypes;
    }

    public Integer getMappingType() {
        return mappingType;
    }

    public void setMappingType(Integer mappingType) {
        this.mappingType = mappingType;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
}
