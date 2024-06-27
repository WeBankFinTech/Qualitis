package com.webank.wedatasphere.qualitis.query.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;

import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
public class StandardValueRequest {

    @JsonProperty("edition_id")
    private String editionId;
    @JsonProperty("cn_name")
    private String cnName;
    @JsonProperty("en_name")
    private String enName;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("modify_user")
    private String modifyUser;

    @JsonProperty("dev_department_id")
    private String devDepartmentId;
    @JsonProperty("ops_department_id")
    private String opsDepartmentId;
    @JsonProperty("action_range")
    private Set<String> actionRange;
    @JsonProperty("source_type")
    private String sourceType;

    private int page;
    private int size;

    public StandardValueRequest() {
        this.page = 0;
        this.size = 15;
    }

    public String getEditionId() {
        return editionId;
    }

    public void setEditionId(String editionId) {
        this.editionId = editionId;
    }

    public Set<String> getActionRange() {
        return actionRange;
    }

    public void setActionRange(Set<String> actionRange) {
        this.actionRange = actionRange;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getDevDepartmentId() {
        return devDepartmentId;
    }

    public void setDevDepartmentId(String devDepartmentId) {
        this.devDepartmentId = devDepartmentId;
    }

    public String getOpsDepartmentId() {
        return opsDepartmentId;
    }

    public void setOpsDepartmentId(String opsDepartmentId) {
        this.opsDepartmentId = opsDepartmentId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public static void checkRequest(StandardValueRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        long page = request.getPage();
        long size = request.getSize();
        if (page < 0) {
            throw new UnExpectedRequestException("page should >= 0, request: " + request);
        }
        if (size <= 0) {
            throw new UnExpectedRequestException("size should > 0, request: " + request);
        }
    }
}
