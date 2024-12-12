package com.webank.wedatasphere.qualitis.dto;

/**
 * @author v_minminghe@webank.com
 * @date 2022-11-14 16:04
 * @description
 */
public class DataVisibilityPermissionDto {

    private String createUser;
    private Long devDepartmentId;
    private Long opsDepartmentId;

    public DataVisibilityPermissionDto(Builder builder) {
        this.createUser = builder.createUser;
        this.devDepartmentId = builder.devDepartmentId;
        this.opsDepartmentId = builder.opsDepartmentId;
    }

    public String getCreateUser() {
        return createUser;
    }

    public Long getDevDepartmentId() {
        return devDepartmentId;
    }

    public Long getOpsDepartmentId() {
        return opsDepartmentId;
    }

    public static class Builder {

        private String createUser;
        private Long devDepartmentId;
        private Long opsDepartmentId;

        public Builder createUser(String createUser) {
            this.createUser = createUser;
            return this;
        }

        public Builder devDepartmentId(Long devDepartmentId) {
            this.devDepartmentId = devDepartmentId;
            return this;
        }

        public Builder opsDepartmentId(Long opsDepartmentId) {
            this.opsDepartmentId = opsDepartmentId;
            return this;
        }

        public DataVisibilityPermissionDto build() {
            return new DataVisibilityPermissionDto(this);
        }

    }
}
