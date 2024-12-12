package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.request.DepartmentSubInfoRequest;

import java.util.List;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
public class AddStandardValueRequest extends AbstractAddRequest {

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
    @JsonProperty("is_available")
    private Long isAvailable;
    @JsonProperty("dev_department_name")
    private String devDepartmentName;
    @JsonProperty("ops_department_name")
    private String opsDepartmentName;
    @JsonProperty("dev_department_id")
    private Long devDepartmentId;
    @JsonProperty("ops_department_id")
    private Long opsDepartmentId;
    @JsonProperty("visibility_department_list")
    private List<DepartmentSubInfoRequest> visibilityDepartmentList;


    @JsonProperty("source_value")
    private Integer sourceValue;
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

    public List<DepartmentSubInfoRequest> getVisibilityDepartmentList() {
        return visibilityDepartmentList;
    }

    public void setVisibilityDepartmentList(List<DepartmentSubInfoRequest> visibilityDepartmentList) {
        this.visibilityDepartmentList = visibilityDepartmentList;
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

    public Integer getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(Integer sourceValue) {
        this.sourceValue = sourceValue;
    }

    public Long getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Long isAvailable) {
        this.isAvailable = isAvailable;
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

    @Override
    public String toString() {
        return "AddStandardValueRequest{" +
                "cnName='" + cnName + '\'' +
                ", enName='" + enName + '\'' +
                ", labelName='" + labelName + '\'' +
                ", actionRange='" + actionRange + '\'' +
                ", content='" + content + '\'' +
                ", source='" + source + '\'' +
                ", approveSystem='" + approveSystem + '\'' +
                ", approveNumber='" + approveNumber + '\'' +
                '}';
    }

    public static void checkRequest(AddStandardValueRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkString(request.getCnName(), "cn_name");
        CommonChecker.checkString(request.getEnName(), "en_name");

        CommonChecker.checkString(request.getDevDepartmentName(), "devDepartmentName");
        CommonChecker.checkString(request.getOpsDepartmentName(), "opsDepartmentName");
        CommonChecker.checkObject(request.getDevDepartmentId(), "devDepartmentId");
        CommonChecker.checkObject(request.getOpsDepartmentId(), "opsDepartmentId");
        CommonChecker.checkObject(request.getSourceValue(), "sourceValue");

    }


}
