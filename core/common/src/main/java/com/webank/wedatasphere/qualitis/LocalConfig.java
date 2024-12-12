package com.webank.wedatasphere.qualitis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author allenzhou@webank.com
 * @date 2021/9/7 16:20
 */
@Configuration
public class LocalConfig {
    /**
     * zn_CN or en.
     */
    @Value("${front_end.local}")
    private String local;
    /**
     * dev or prod.
     */
    @Value("${front_end.support_migrate}")
    private Boolean supportMigrate;
    /**
     * BDAP or BDP.
     */
    @Value("${front_end.cluster}")
    private String cluster;

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Boolean getSupportMigrate() {
        return supportMigrate;
    }

    public void setSupportMigrate(Boolean supportMigrate) {
        this.supportMigrate = supportMigrate;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }
}
