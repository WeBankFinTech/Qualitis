package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.TemplateDataSourceTypeDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.TemplateDataSourceTypeRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateDataSourceType;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author allenzhou
 */
@Repository
public class TemplateDataSourceTypeDaoImpl implements TemplateDataSourceTypeDao {
    @Autowired
    private TemplateDataSourceTypeRepository templateDataSourceTypeRepository;

    @Override
    public List<Template> findByDataSourceTypeId(Long templateDataSourceId) {
        return templateDataSourceTypeRepository.findByDataSourceTypeId(templateDataSourceId);
    }

    @Override
    public TemplateDataSourceType save(TemplateDataSourceType templateDataSourceType) {
        return templateDataSourceTypeRepository.save(templateDataSourceType);
    }

    @Override
    public void delete(TemplateDataSourceType templateDataSourceType) {
        templateDataSourceTypeRepository.delete(templateDataSourceType);
    }

    @Override
    public void deleteByTemplate(Template template) {
        templateDataSourceTypeRepository.deleteByTemplate(template);
    }

    @Override
    public List<TemplateDataSourceType> findByTemplate(Template template) {
        return templateDataSourceTypeRepository.findByTemplate(template);
    }

}
