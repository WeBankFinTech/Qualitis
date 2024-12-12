package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateDataSourceTypeEnum;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * @author allenzhou@webank.com
 * @date 2021/11/4 14:45
 */
public class TemplatePageRequest extends PageRequest {

    @JsonProperty("template_id")
    private Long templateId;
    @JsonProperty("description")
    private String description;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("cn_name")
    private String cnName;
    @JsonProperty("en_name")
    private String enName;
    @JsonProperty("data_source_type")
    private String dataSourceType;
    @JsonProperty("verification_level")
    private Long verificationLevel;
    @JsonProperty("verification_type")
    private Long verificationType;
    @JsonProperty("create_name")
    private String createName;
    @JsonProperty("modify_name")
    private String modifyName;
    @JsonProperty("dev_department_id")
    private Long devDepartmentId;
    @JsonProperty("ops_department_id")
    private Long opsDepartmentId;
    @JsonProperty("template_type")
    private Integer templateType;

    @JsonProperty("action_range")
    private Set<String> actionRange;

    @JsonProperty("create_start_time")
    private String createStartTime;
    @JsonProperty("create_end_time")
    private String createEndTime;
    @JsonProperty("modify_start_time")
    private String modifyStartTime;
    @JsonProperty("modify_end_time")
    private String modifyEndTime;

    public TemplatePageRequest() {
        // Do nothing
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getCreateStartTime() {
        return createStartTime;
    }

    public void setCreateStartTime(String createStartTime) {
        this.createStartTime = createStartTime;
    }

    public String getCreateEndTime() {
        return createEndTime;
    }

    public void setCreateEndTime(String createEndTime) {
        this.createEndTime = createEndTime;
    }

    public String getModifyStartTime() {
        return modifyStartTime;
    }

    public void setModifyStartTime(String modifyStartTime) {
        this.modifyStartTime = modifyStartTime;
    }

    public String getModifyEndTime() {
        return modifyEndTime;
    }

    public void setModifyEndTime(String modifyEndTime) {
        this.modifyEndTime = modifyEndTime;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getDataSourceType() {
        return dataSourceType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public Long getVerificationLevel() {
        return verificationLevel;
    }

    public void setVerificationLevel(Long verificationLevel) {
        this.verificationLevel = verificationLevel;
    }

    public Long getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(Long verificationType) {
        this.verificationType = verificationType;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getModifyName() {
        return modifyName;
    }

    public void setModifyName(String modifyName) {
        this.modifyName = modifyName;
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

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public Integer getTemplateType() {
        return templateType;
    }

    public void setTemplateType(Integer templateType) {
        this.templateType = templateType;
    }

    public Set<String> getActionRange() {
        return actionRange;
    }

    public void setActionRange(Set<String> actionRange) {
        this.actionRange = actionRange;
    }

    public void checkRequest() {
        if (StringUtils.isEmpty(this.cnName)) {
            this.cnName = null;
        } else {
            this.cnName = SpecCharEnum.PERCENT.getValue() + this.cnName + SpecCharEnum.PERCENT.getValue();
        }

        if (StringUtils.isEmpty(this.enName)) {
            this.enName = null;
        } else {
            this.enName = SpecCharEnum.PERCENT.getValue() + this.enName + SpecCharEnum.PERCENT.getValue();
        }

        if (StringUtils.isNotEmpty(this.createName)) {
            User user = SpringContextHolder.getBean(UserDao.class).findByUsername(this.createName);
            if (user != null) {
                this.createName = String.valueOf(user.getId());
            }
        }
        if (StringUtils.isNotEmpty(this.modifyName)) {
            User user = SpringContextHolder.getBean(UserDao.class).findByUsername(this.modifyName);
            if (user != null) {
                this.modifyName = String.valueOf(user.getId());
            }
        }
    }

    @Override
    public String toString() {
        return "TemplatePageRequest{" +
                "userName='" + userName + '\'' +
                ", cnName='" + cnName + '\'' +
                ", enName='" + enName + '\'' +
                ", dataSourceType='" + dataSourceType + '\'' +
                ", verificationLevel=" + verificationLevel +
                ", verificationType=" + verificationType +
                ", createName='" + createName + '\'' +
                ", modifyName='" + modifyName + '\'' +
                ", devDepartmentId=" + devDepartmentId +
                ", opsDepartmentId=" + opsDepartmentId +
                ", templateType=" + templateType +
                ", actionRange=" + actionRange +
                "} " + super.toString();
    }
}
