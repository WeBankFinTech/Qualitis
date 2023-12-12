package com.webank.wedatasphere.qualitis.rule.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author allenzhou
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_template_datasource_type")
public class TemplateDataSourceType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "data_source_type_id")
    private Integer dataSourceTypeId;

    @ManyToOne
    @JsonIgnore
    private Template template;

    public TemplateDataSourceType() {
    }

    public TemplateDataSourceType(Integer dataSourceTypeId, Template template) {
        this.dataSourceTypeId = dataSourceTypeId;
        this.template = template;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TemplateDataSourceType that = (TemplateDataSourceType) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(dataSourceTypeId, that.dataSourceTypeId) &&
            Objects.equals(template, that.template);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dataSourceTypeId, template);
    }
}
