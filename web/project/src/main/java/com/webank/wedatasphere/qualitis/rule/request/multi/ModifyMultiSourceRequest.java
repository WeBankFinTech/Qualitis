/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.rule.request.multi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.rule.request.AbstractCommonRequest;
import com.webank.wedatasphere.qualitis.rule.request.AlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplateArgumentRequest;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author howeye
 */
public class ModifyMultiSourceRequest extends AbstractCommonRequest {
    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("multi_source_rule_template_id")
    private Long multiSourceRuleTemplateId;
    private MultiDataSourceConfigRequest source;
    private MultiDataSourceConfigRequest target;

    //    private List<MultiDataSourceJoinConfigRequest> mappings;
//    @JsonProperty("compare_cols")
//    private List<MultiDataSourceJoinConfigRequest> compareCols;
//    private String filter;

    @JsonProperty("template_arguments")
    private List<TemplateArgumentRequest> templateArgumentRequests;

    @JsonProperty("contrast_type")
    private Integer contrastType;
    @JsonProperty("filter_col_names")
    private List<DataSourceColumnRequest> colNames;


    @JsonProperty("alarm_variable")
    private List<AlarmConfigRequest> alarmVariable;

    @JsonProperty("left_linkis_udf_names")
    private List<String> leftLinkisUdfNames;

    @JsonProperty("right_linkis_udf_names")
    private List<String> rightLinkisUdfNames;


    public ModifyMultiSourceRequest() {
        // Default Constructor
    }

    public List<String> getLeftLinkisUdfNames() {
        return leftLinkisUdfNames;
    }

    public void setLeftLinkisUdfNames(List<String> leftLinkisUdfNames) {
        this.leftLinkisUdfNames = leftLinkisUdfNames;
    }

    public List<String> getRightLinkisUdfNames() {
        return rightLinkisUdfNames;
    }

    public void setRightLinkisUdfNames(List<String> rightLinkisUdfNames) {
        this.rightLinkisUdfNames = rightLinkisUdfNames;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public Long getMultiSourceRuleTemplateId() {
        return multiSourceRuleTemplateId;
    }

    public void setMultiSourceRuleTemplateId(Long multiSourceRuleTemplateId) {
        this.multiSourceRuleTemplateId = multiSourceRuleTemplateId;
    }

    public MultiDataSourceConfigRequest getSource() {
        return source;
    }

    public void setSource(MultiDataSourceConfigRequest source) {
        this.source = source;
    }

    public MultiDataSourceConfigRequest getTarget() {
        return target;
    }

    public void setTarget(MultiDataSourceConfigRequest target) {
        this.target = target;
    }

    public List<AlarmConfigRequest> getAlarmVariable() {
        return alarmVariable;
    }

    public void setAlarmVariable(List<AlarmConfigRequest> alarmVariable) {
        this.alarmVariable = alarmVariable;
    }

    public Integer getContrastType() {
        return contrastType;
    }

    public void setContrastType(Integer contrastType) {
        this.contrastType = contrastType;
    }

    public List<DataSourceColumnRequest> getColNames() {
        return colNames;
    }

    public void setColNames(List<DataSourceColumnRequest> colNames) {
        this.colNames = colNames;
    }

    public List<TemplateArgumentRequest> getTemplateArgumentRequests() {
        return templateArgumentRequests;
    }

    public void setTemplateArgumentRequests(List<TemplateArgumentRequest> templateArgumentRequests) {
        this.templateArgumentRequests = templateArgumentRequests;
    }

    public static void checkRequest(ModifyMultiSourceRequest request, Boolean cs, Boolean isCustomConsistence, Boolean tableStructureConsistent) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkObject(request.getRuleId(), "Rule id");
        AddMultiSourceRuleRequest addMultiSourceRuleRequest = new AddMultiSourceRuleRequest();

        BeanUtils.copyProperties(request, addMultiSourceRuleRequest);
        AddMultiSourceRuleRequest.checkRequest(addMultiSourceRuleRequest, true, cs, isCustomConsistence, tableStructureConsistent);
    }

    @Override
    public String toString() {
        return "ModifyMultiSourceRequest{" +
                "clusterName='" + clusterName + '\'' +
                ", multiSourceRuleTemplateId=" + multiSourceRuleTemplateId +
                ", source=" + source +
                ", target=" + target +
                ", templateArgumentRequests=" + templateArgumentRequests +
                ", contrastType=" + contrastType +
                ", colNames=" + colNames +
                ", alarmVariable=" + alarmVariable +
                ", leftLinkisUdfNames=" + leftLinkisUdfNames +
                ", rightLinkisUdfNames=" + rightLinkisUdfNames +
                "} " + super.toString();
    }
}
