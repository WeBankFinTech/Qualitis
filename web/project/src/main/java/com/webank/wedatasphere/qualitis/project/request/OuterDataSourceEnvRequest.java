package com.webank.wedatasphere.qualitis.project.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.request.LinkisConnectParamsRequest;
import org.apache.commons.lang.StringUtils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author v_minminghe@webank.com
 * @date 2023-10-17 14:53
 * @description
 */
public class OuterDataSourceEnvRequest {

    /**
     * 环境ID
     */
    private Long id;
    /**
     * 环境名称
     */
    @JsonProperty(value = "env_name", required = true)
    private String envName;
    /**
     * 环境描述
     */
    @JsonProperty("env_desc")
    private String envDesc;
    @JsonProperty("dcn_num")
    private String dcnNum;
    @JsonProperty("logic_area")
    private String logicArea;

    /**
     * 数据库实例
     */
    @JsonProperty("database_instance")
    private String databaseInstance;
    /**
     * 非共享登录认证信息
     */
    @JsonProperty(value = "connect_params", required = true)
    private LinkisConnectParamsRequest connectParams;

    public String getDatabaseInstance() {
        return databaseInstance;
    }

    public void setDatabaseInstance(String databaseInstance) {
        this.databaseInstance = databaseInstance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public String getEnvDesc() {
        return envDesc;
    }

    public void setEnvDesc(String envDesc) {
        this.envDesc = envDesc;
    }

    public String getDcnNum() {
        return dcnNum;
    }

    public void setDcnNum(String dcnNum) {
        this.dcnNum = dcnNum;
    }

    public String getLogicArea() {
        return logicArea;
    }

    public void setLogicArea(String logicArea) {
        this.logicArea = logicArea;
    }

    public LinkisConnectParamsRequest getConnectParams() {
        return connectParams;
    }

    public void setConnectParams(LinkisConnectParamsRequest connectParams) {
        this.connectParams = connectParams;
    }

    public void checkRequest() throws UnExpectedRequestException {
        try {
            ParameterChecker.checkEmpty(this);
            validateConnectionParameters();
        } catch (IllegalAccessException e) {
            throw new UnExpectedRequestException("Failed to validate request parameters!");
        }
    }

    private void validateConnectionParameters() throws UnExpectedRequestException {
        LinkisConnectParamsRequest linkisConnectParamsRequest = this.connectParams;
        CommonChecker.checkString(linkisConnectParamsRequest.getHost(), "host");
        CommonChecker.checkString(linkisConnectParamsRequest.getPort(), "port");
        Pattern pattern = Pattern.compile("^[^,:]*$");
        Matcher matcher = pattern.matcher(this.envName);
        if (!matcher.matches()) {
            throw new UnExpectedRequestException("Invalid envName!");
        }

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
