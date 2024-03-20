package com.webank.wedatasphere.qualitis.metadata.response.datasource;

/**
 * @author v_minminghe@webank.com
 * @date 2022-08-30 15:21
 * @description
 */
public class LinkisDataSourceTypeDetail {

    private String id;
    /** Name */
    private String name;
    /** Description */
    private String description;
    /** The display name of the type */
    private String option;
    /** classifier */
    private String classifier;
    /** Icon url */
    private String icon;
    /**
     * Tells the user the number of levels for the datasource eg: for mysql/hive/presto datasource:
     * (datasource) --> database --> tables --> column 3 for kafka datasource: (datasource) --> topic
     * --> partition 2
     */
    private int layers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getLayers() {
        return layers;
    }

    public void setLayers(int layers) {
        this.layers = layers;
    }
}
