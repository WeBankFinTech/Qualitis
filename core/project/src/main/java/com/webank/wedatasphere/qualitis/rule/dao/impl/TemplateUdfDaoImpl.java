package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.TemplateUdfDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.TemplateUdfRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateUdf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
@Repository
public class TemplateUdfDaoImpl implements TemplateUdfDao {
    @Autowired
    private TemplateUdfRepository templateUdfRepository;

    @Override
    public List<TemplateUdf> findByRuleTemplate(Template template) {
        return templateUdfRepository.findByTemplate(template);
    }

    @Override
    public TemplateUdf findById(Long templateUdfFunctionId) {
        return templateUdfRepository.findById(templateUdfFunctionId).orElse(null);
    }

    @Override
    public Set<TemplateUdf> saveAll(List<TemplateUdf> templateUdfFunctions) {
        Set<TemplateUdf> result = new HashSet<>();
        result.addAll(templateUdfRepository.saveAll(templateUdfFunctions));
        return result;
    }

    @Override
    public void deleteByTemplate(Template templateInDb) {
        templateUdfRepository.deleteByTemplate(templateInDb);
    }
}
