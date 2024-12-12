package com.webank.wedatasphere.qualitis.rule.entity;

import com.webank.wedatasphere.qualitis.entity.Department;
import java.util.Objects;
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
@Table(name = "qualitis_template_department")
public class TemplateDepartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private Department department;
    @ManyToOne
    private Template template;

    public TemplateDepartment() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
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
        TemplateDepartment that = (TemplateDepartment) o;
        return id == that.id &&
            Objects.equals(department, that.department) &&
            Objects.equals(template, that.template);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, department, template);
    }
}
