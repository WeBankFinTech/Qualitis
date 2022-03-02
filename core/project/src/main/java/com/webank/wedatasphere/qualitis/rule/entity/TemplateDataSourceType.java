package com.webank.wedatasphere.qualitis.rule.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author allenzhou
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_template_datasource_type")
public class TemplateDataSourceType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "data_source_type_id")
    private Integer dataSourceTypeId;
    @ManyToOne
    private Template template;

    public TemplateDataSourceType() {
    }

    public TemplateDataSourceType(Integer dataSourceTypeId, Template template) {
        this.dataSourceTypeId = dataSourceTypeId;
        this.template = template;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getDataSourceTypeId() {
        return dataSourceTypeId;
    }

    public void setDataSourceTypeId(Integer dataSourceTypeId) {
        this.dataSourceTypeId = dataSourceTypeId;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

}
