package com.webank.wedatasphere.qualitis.rule.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.response.DepartmentSubInfoResponse;
import com.webank.wedatasphere.qualitis.rule.constant.StandardSourceEnum;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueLabelVersion;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueVersion;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
public class StandardValueResponse {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("standard_value_id")
    private Long standardValueId;
    @JsonProperty("cn_name")
    private String cnName;
    @JsonProperty("en_name")
    private String enName;
    @JsonProperty("label_name")
    private Set<String> labelName;
    @JsonProperty("action_range")
    private Set<String> actionRange;
    @JsonProperty("content")
    private String content;
    @JsonProperty("source")
    private String source;
    @JsonProperty("approve_system")
    private String approveSystem;
    @JsonProperty("approve_number")
    private String approveNumber;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("modify_user")
    private String modifyUser;
    @JsonProperty("modify_time")
    private String modifyTime;
    @JsonProperty("edition_id")
    private Long editionId;
    @JsonProperty("dev_department_name")
    private String devDepartmentName;
    @JsonProperty("ops_department_name")
    private String opsDepartmentName;
    @JsonProperty("dev_department_id")
    private Long devDepartmentId;
    @JsonProperty("ops_department_id")
    private Long opsDepartmentId;
    @JsonProperty("visibility_department_list")
    private List<DepartmentSubInfoResponse> visibilityDepartmentList;
    @JsonProperty("is_editable")
    private boolean isEditable;

    @JsonProperty("source_value")
    private Integer sourceValue;
    @JsonProperty("source_value_name")
    private String sourceValueName;

    @JsonProperty("std_sub_code")
    private String stdSubCode;
    @JsonProperty("std_sub_name")
    private String stdSubName;
    @JsonProperty("std_big_category_code")
    private String stdBigCategoryCode;
    @JsonProperty("std_big_category_name")
    private String stdBigCategoryName;
    @JsonProperty("small_category_code")
    private String smallCategoryCode;
    @JsonProperty("small_category_name")
    private String smallCategoryName;

    @JsonProperty("std_cn_name")
    private String stdCnName;
    @JsonProperty("code")
    private String code;
    @JsonProperty("code_name")
    private String codeName;
    @JsonProperty("std_small_category_urn")
    private String stdSmallCategoryUrn;


    public StandardValueResponse() {
    }

    public StandardValueResponse(StandardValueVersion standardValueVersion) {
        this.stdSubCode = standardValueVersion.getStdSubCode();
        this.stdSubName = standardValueVersion.getStdSubName();
        this.stdBigCategoryCode = standardValueVersion.getStdBigCategoryCode();
        this.stdBigCategoryName = standardValueVersion.getStdBigCategoryName();
        this.smallCategoryCode = standardValueVersion.getSmallCategoryCode();
        this.smallCategoryName = standardValueVersion.getSmallCategoryName();
        this.stdCnName = standardValueVersion.getStdCnName();
        this.code = standardValueVersion.getCode();
        this.codeName = standardValueVersion.getCodeName();
        this.stdSmallCategoryUrn = standardValueVersion.getStdSmallCategoryUrn();

        this.cnName = standardValueVersion.getCnName();
        this.enName = standardValueVersion.getEnName();

        Set<StandardValueLabelVersion> labelSet = standardValueVersion.getStandardValueLabelVersion();
        if (labelSet != null && !labelSet.isEmpty()) {
            Set<String> labels = new HashSet<>();
            for (StandardValueLabelVersion standardValueLabel : labelSet) {
                labels.add(standardValueLabel.getLabelName());
            }
            this.labelName = labels;
        }
        this.content = standardValueVersion.getContent();
        //来源
        if (standardValueVersion.getSourceValue() == null || standardValueVersion.getSourceValue().equals(StandardSourceEnum.CUSTOM_SOURCE.getCode())) {
            this.source = standardValueVersion.getSource();
            this.sourceValue = standardValueVersion.getSourceValue() == null ? null : standardValueVersion.getSourceValue();
            this.sourceValueName = standardValueVersion.getSourceValue() == null ? null : StandardSourceEnum.getStandardSourceByCode(standardValueVersion.getSourceValue());
        } else if (standardValueVersion.getSourceValue().equals(StandardSourceEnum.DATA_SHAPIS.getCode())) {
            this.source = standardValueVersion.getStdCnName();
            this.sourceValue = standardValueVersion.getSourceValue();
            this.sourceValueName = StandardSourceEnum.getStandardSourceByCode(standardValueVersion.getSourceValue());
        }

        this.createUser = standardValueVersion.getCreateUser();
        this.createTime = standardValueVersion.getCreateTime();
        this.modifyTime = standardValueVersion.getModifyTime();
        this.modifyUser = standardValueVersion.getModifyUser();
        this.editionId = standardValueVersion.getId();
        this.id = standardValueVersion.getId();
        this.devDepartmentName = standardValueVersion.getDevDepartmentName();
        this.opsDepartmentName = standardValueVersion.getOpsDepartmentName();
        this.devDepartmentId = standardValueVersion.getDevDepartmentId();
        this.opsDepartmentId = standardValueVersion.getOpsDepartmentId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public Long getDevDepartmentId() {
        return devDepartmentId;
    }

    public void setDevDepartmentId(Long devDepartmentId) {
        this.devDepartmentId = devDepartmentId;
    }

    public Long getOpsDepartmentId() {
        return opsDepartmentId;
    }

    public void setOpsDepartmentId(Long opsDepartmentId) {
        this.opsDepartmentId = opsDepartmentId;
    }

    public String getDevDepartmentName() {
        return devDepartmentName;
    }

    public void setDevDepartmentName(String devDepartmentName) {
        this.devDepartmentName = devDepartmentName;
    }

    public String getOpsDepartmentName() {
        return opsDepartmentName;
    }

    public void setOpsDepartmentName(String opsDepartmentName) {
        this.opsDepartmentName = opsDepartmentName;
    }

    public List<DepartmentSubInfoResponse> getVisibilityDepartmentList() {
        return visibilityDepartmentList;
    }

    public void setVisibilityDepartmentList(List<DepartmentSubInfoResponse> visibilityDepartmentList) {
        this.visibilityDepartmentList = visibilityDepartmentList;
    }

    public Long getStandardValueId() {
        return standardValueId;
    }

    public void setStandardValueId(Long standardValueId) {
        this.standardValueId = standardValueId;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public Set<String> getLabelName() {
        return labelName;
    }

    public void setLabelName(Set<String> labelName) {
        this.labelName = labelName;
    }

    public Set<String> getActionRange() {
        return actionRange;
    }

    public void setActionRange(Set<String> actionRange) {
        this.actionRange = actionRange;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getApproveSystem() {
        return approveSystem;
    }

    public void setApproveSystem(String approveSystem) {
        this.approveSystem = approveSystem;
    }

    public String getApproveNumber() {
        return approveNumber;
    }

    public void setApproveNumber(String approveNumber) {
        this.approveNumber = approveNumber;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Long getEditionId() {
        return editionId;
    }

    public void setEditionId(Long editionId) {
        this.editionId = editionId;
    }

    public Integer getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(Integer sourceValue) {
        this.sourceValue = sourceValue;
    }

    public String getSourceValueName() {
        return sourceValueName;
    }

    public void setSourceValueName(String sourceValueName) {
        this.sourceValueName = sourceValueName;
    }

    public String getStdSubCode() {
        return stdSubCode;
    }

    public void setStdSubCode(String stdSubCode) {
        this.stdSubCode = stdSubCode;
    }

    public String getStdSubName() {
        return stdSubName;
    }

    public void setStdSubName(String stdSubName) {
        this.stdSubName = stdSubName;
    }

    public String getStdBigCategoryCode() {
        return stdBigCategoryCode;
    }

    public void setStdBigCategoryCode(String stdBigCategoryCode) {
        this.stdBigCategoryCode = stdBigCategoryCode;
    }

    public String getStdBigCategoryName() {
        return stdBigCategoryName;
    }

    public void setStdBigCategoryName(String stdBigCategoryName) {
        this.stdBigCategoryName = stdBigCategoryName;
    }

    public String getSmallCategoryCode() {
        return smallCategoryCode;
    }

    public void setSmallCategoryCode(String smallCategoryCode) {
        this.smallCategoryCode = smallCategoryCode;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getStdSmallCategoryUrn() {
        return stdSmallCategoryUrn;
    }

    public void setStdSmallCategoryUrn(String stdSmallCategoryUrn) {
        this.stdSmallCategoryUrn = stdSmallCategoryUrn;
    }
}
