package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.response.RuleTemplateResponse;

/**
 * @author v_minminghe@webank.com
 * @date 2024-04-18 9:38
 * @description
 */
public class MetricTemplateQueryResponse extends RuleTemplateResponse {

    @JsonProperty("sql_action")
    private String sqlAction;
    @JsonIgnore
    private Long calcuUnitId;

    public MetricTemplateQueryResponse(Template template) {
        super(template);
        this.calcuUnitId = template.getCalcuUnitId();
    }

    public Long getCalcuUnitId() {
        return calcuUnitId;
    }

    public void setCalcuUnitId(Long calcuUnitId) {
        this.calcuUnitId = calcuUnitId;
    }

    public String getSqlAction() {
        return sqlAction;
    }

    public void setSqlAction(String sqlAction) {
        this.sqlAction = sqlAction;
    }
}
