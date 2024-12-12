package com.webank.wedatasphere.qualitis.request;

/**
 * @author allenzhou@webank.com
 * @date 2021/11/2 10:50
 */
public class DataSourceType {
    private Integer layers;
    private String name;

    public DataSourceType() {
    }

    public Integer getLayers() {
        return layers;
    }

    public void setLayers(Integer layers) {
        this.layers = layers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
