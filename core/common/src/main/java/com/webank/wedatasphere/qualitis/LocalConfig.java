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
    @Value("${front_end.center}")
    private String center;

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }
}
