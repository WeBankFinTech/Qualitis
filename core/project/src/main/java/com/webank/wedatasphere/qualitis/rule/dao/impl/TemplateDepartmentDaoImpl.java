package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateDepartmentDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.TemplateDepartmentRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateDepartment;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author allenzhou
 */
@Repository
public class TemplateDepartmentDaoImpl implements TemplateDepartmentDao {
    @Autowired
    private TemplateDepartmentRepository templateDepartmentRepository;

    @Override
    public TemplateDepartment saveDepartmentTemplate(TemplateDepartment templateDepartment) {
        return templateDepartmentRepository.save(templateDepartment);
    }

    @Override
    public void delete(TemplateDepartment templateDepartment) {
        templateDepartmentRepository.delete(templateDepartment);
    }

    @Override
    public TemplateDepartment findByTemplate(Template templateInDb) {
        return templateDepartmentRepository.findBytemplate(templateInDb);
    }

    @Override
    public List<Template> findTemplates(Department department) {
        return templateDepartmentRepository.findTemplates(department);
    }
}
