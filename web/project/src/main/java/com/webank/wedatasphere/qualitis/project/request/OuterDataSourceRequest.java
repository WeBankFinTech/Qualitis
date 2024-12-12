package com.webank.wedatasphere.qualitis.project.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.request.LinkisConnectParamsRequest;
import com.webank.wedatasphere.qualitis.request.DepartmentSubInfoRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2023-10-17 14:52
 * @description
 */
public class OuterDataSourceRequest {

    @JsonProperty(value = "data_source_name", required = true)
    private String dataSourceName;
    @JsonProperty(value = "data_source_type_name", required = true)
    private String dataSourceTypeName;
    @JsonProperty(value = "operate_user", required = true)
    private String username;
    @JsonProperty(value = "sub_system")
    private String subSystemName;
    @JsonProperty("sub_system_id")
    private String subSystemId;
    @JsonProperty(value = "input_type", required = true)
    private Integer inputType;
    @JsonProperty(value = "verify_type", required = true)
    private Integer verifyType;
    @JsonProperty("connect_params")
    private LinkisConnectParamsRequest connectParams;
    @JsonProperty("data_source_desc")
    private String dataSourceDesc;
    private String labels;
    @JsonProperty("visibility_department_list")
    private List<DepartmentSubInfoRequest> visibilityDepartmentList;
    @JsonProperty("create_system")
    private String createSystem = "Qualitis";
    @JsonProperty("dcn_range_type")
    private String dcnRangeType;
    @JsonProperty("dcn_range_values")
    private List<String> dcnRangeValues;

    @JsonProperty(value = "datasource_envs")
    private List<OuterDataSourceEnvRequest> dataSourceEnvs;

    public String getSubSystemId() {
        return subSystemId;
    }

    public void setSubSystemId(String subSystemId) {
        this.subSystemId = subSystemId;
    }

    public String getDcnRangeType() {
        return dcnRangeType;
    }

    public void setDcnRangeType(String dcnRangeType) {
        this.dcnRangeType = dcnRangeType;
    }

    public List<String> getDcnRangeValues() {
        return dcnRangeValues;
    }

    public void setDcnRangeValues(List<String> dcnRangeValues) {
        this.dcnRangeValues = dcnRangeValues;
    }

    public List<DepartmentSubInfoRequest> getVisibilityDepartmentList() {
        return visibilityDepartmentList;
    }

    public void setVisibilityDepartmentList(List<DepartmentSubInfoRequest> visibilityDepartmentList) {
        this.visibilityDepartmentList = visibilityDepartmentList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSubSystemName() {
        return subSystemName;
    }

    public void setSubSystemName(String subSystemName) {
        this.subSystemName = subSystemName;
    }

    public Integer getInputType() {
        return inputType;
    }

    public void setInputType(Integer inputType) {
        this.inputType = inputType;
    }

    public Integer getVerifyType() {
        return verifyType;
    }

    public void setVerifyType(Integer verifyType) {
        this.verifyType = verifyType;
    }

    public LinkisConnectParamsRequest getConnectParams() {
        return connectParams;
    }

    public void setConnectParams(LinkisConnectParamsRequest connectParams) {
        this.connectParams = connectParams;
    }

    public String getDataSourceTypeName() {
        return dataSourceTypeName;
    }

    public void setDataSourceTypeName(String dataSourceTypeName) {
        this.dataSourceTypeName = dataSourceTypeName;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getDataSourceDesc() {
        return dataSourceDesc;
    }

    public void setDataSourceDesc(String dataSourceDesc) {
        this.dataSourceDesc = dataSourceDesc;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getCreateSystem() {
        return createSystem;
    }

    public void setCreateSystem(String createSystem) {
        this.createSystem = createSystem;
    }

    public List<OuterDataSourceEnvRequest> getDataSourceEnvs() {
        return dataSourceEnvs;
    }

    public void setDataSourceEnvs(List<OuterDataSourceEnvRequest> dataSourceEnvs) {
        this.dataSourceEnvs = dataSourceEnvs;
    }

    public void checkRequest() throws UnExpectedRequestException {
        try {
            ParameterChecker.checkEmpty(this);
            if (StringUtils.isBlank(this.subSystemName) && null == this.subSystemId) {
                throw new UnExpectedRequestException("sub_system must be not null!");
            }
            if (!Integer.valueOf(QualitisConstants.DATASOURCE_MANAGER_INPUT_TYPE_AUTO).equals(this.inputType)
                    && !Integer.valueOf(QualitisConstants.DATASOURCE_MANAGER_INPUT_TYPE_MANUAL).equals(this.inputType)) {
                throw new UnExpectedRequestException("Error Parameter: input_type");
            }
            if (!Integer.valueOf(QualitisConstants.DATASOURCE_MANAGER_VERIFY_TYPE_SHARE).equals(this.verifyType)
                    && !Integer.valueOf(QualitisConstants.DATASOURCE_MANAGER_VERIFY_TYPE_NON_SHARE).equals(this.verifyType)) {
                throw new UnExpectedRequestException("Error Parameter: verify_type");
            }
            if (Integer.valueOf(QualitisConstants.DATASOURCE_MANAGER_INPUT_TYPE_AUTO).equals(this.inputType)
                    && Integer.valueOf(QualitisConstants.DATASOURCE_MANAGER_VERIFY_TYPE_NON_SHARE).equals(this.verifyType)) {
                throw new UnExpectedRequestException("Error Parameter: verify_type");
            }

//            validate shared connect_params
            if (Integer.valueOf(QualitisConstants.DATASOURCE_MANAGER_VERIFY_TYPE_SHARE).equals(this.verifyType)) {
                validateConnectionParameters();
            }

            if (StringUtils.isNotBlank(dcnRangeType)) {
                if (!Integer.valueOf(QualitisConstants.DATASOURCE_MANAGER_INPUT_TYPE_AUTO).equals(this.inputType)) {
                    throw new UnExpectedRequestException("Error Parameter: input_type");
                }
                if (!Arrays.asList("all", QualitisConstants.CMDB_KEY_DCN_NUM, QualitisConstants.CMDB_KEY_LOGIC_AREA).contains(dcnRangeType)) {
                    throw new UnExpectedRequestException("Error Parameter: dcn_range_type");
                }
                if (Arrays.asList(QualitisConstants.CMDB_KEY_DCN_NUM, QualitisConstants.CMDB_KEY_LOGIC_AREA).contains(dcnRangeType)
                        && CollectionUtils.isEmpty(dcnRangeValues)) {
                    throw new UnExpectedRequestException("Parameter must be not null: dcn_range_values");
                }
            } else {
                CommonChecker.checkListMinSize(this.dataSourceEnvs, 1, "datasource_envs");
                List<OuterDataSourceEnvRequest> dataSourceEnvRequestList = this.dataSourceEnvs;
                for (OuterDataSourceEnvRequest dataSourceEnvRequest : dataSourceEnvRequestList) {
                    dataSourceEnvRequest.checkRequest();

                    if (Integer.valueOf(QualitisConstants.DATASOURCE_MANAGER_INPUT_TYPE_AUTO).equals(this.inputType)) {
                        CommonChecker.checkString(dataSourceEnvRequest.getDatabaseInstance(), "database_instance");
                    }

                }
            }
        } catch (IllegalAccessException e) {
            throw new UnExpectedRequestException("Failed to validate request parameters!");
        }
    }

    private void validateConnectionParameters() throws UnExpectedRequestException {
        CommonChecker.checkObject(this.connectParams, "connect_params");
        LinkisConnectParamsRequest linkisConnectParamsRequest = this.connectParams;

        if (StringUtils.isNotBlank(linkisConnectParamsRequest.getAuthType())
                && !QualitisConstants.AUTH_TYPE_ACCOUNT_PWD.equals(linkisConnectParamsRequest.getAuthType())
                && !QualitisConstants.AUTH_TYPE_DPM.equals(linkisConnectParamsRequest.getAuthType())) {
            throw new UnExpectedRequestException("Error Parameter: auth_type");
        }

        if (QualitisConstants.AUTH_TYPE_DPM.equals(linkisConnectParamsRequest.getAuthType())) {
            CommonChecker.checkString(linkisConnectParamsRequest.getMkPrivate(), "mkPrivate");
            CommonChecker.checkString(linkisConnectParamsRequest.getAppId(), "appId");
            CommonChecker.checkString(linkisConnectParamsRequest.getObjectId(), "objectId");
        } else if (QualitisConstants.AUTH_TYPE_ACCOUNT_PWD.equals(connectParams.getAuthType())) {
            CommonChecker.checkString(linkisConnectParamsRequest.getUsername(), "username");
            CommonChecker.checkString(linkisConnectParamsRequest.getPassword(), "password");
        }
    }

}
