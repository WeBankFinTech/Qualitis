package com.webank.wedatasphere.qualitis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author allenzhou
 */
@Configuration
public class TaskDataSourceConfig {
    @Value("${task.persistent.private_key}")
    private String privateKey;
    @Value("${task.persistent.username}")
    private String username;
    @Value("${task.persistent.password}")
    private String password;
    @Value("${task.persistent.mysqlsec_open}")
    private Boolean mysqlsecOpen;
    @Value("${task.persistent.mysqlsec}")
    private String mysqlsec;
    @Value("${task.persistent.hive_sort_udf_open}")
    private Boolean hiveSortUdfOpen;
    @Value("${task.persistent.hive_sort_udf}")
    private String hiveSortUdf;
    @Value("${task.persistent.hive_sort_udf_class_path}")
    private String hiveSortUdfClassPath;
    @Value("${task.persistent.hive_sort_udf_lib_path}")
    private String hiveSortUdfLibPath;

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getMysqlsecOpen() {
        return mysqlsecOpen;
    }

    public void setMysqlsecOpen(Boolean mysqlsecOpen) {
        this.mysqlsecOpen = mysqlsecOpen;
    }

    public String getMysqlsec() {
        return mysqlsec;
    }

    public void setMysqlsec(String mysqlsec) {
        this.mysqlsec = mysqlsec;
    }

    public Boolean getHiveSortUdfOpen() {
        return hiveSortUdfOpen;
    }

    public void setHiveSortUdfOpen(Boolean hiveSortUdfOpen) {
        this.hiveSortUdfOpen = hiveSortUdfOpen;
    }

    public String getHiveSortUdf() {
        return hiveSortUdf;
    }

    public void setHiveSortUdf(String hiveSortUdf) {
        this.hiveSortUdf = hiveSortUdf;
    }

    public String getHiveSortUdfClassPath() {
        return hiveSortUdfClassPath;
    }

    public void setHiveSortUdfClassPath(String hiveSortUdfClassPath) {
        this.hiveSortUdfClassPath = hiveSortUdfClassPath;
    }

    public String getHiveSortUdfLibPath() {
        return hiveSortUdfLibPath;
    }

    public void setHiveSortUdfLibPath(String hiveSortUdfLibPath) {
        this.hiveSortUdfLibPath = hiveSortUdfLibPath;
    }
}
