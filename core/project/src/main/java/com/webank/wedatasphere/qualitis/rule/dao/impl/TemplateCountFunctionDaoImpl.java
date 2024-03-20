package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.TemplateCountFunctionDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.TemplateCountFunctionRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateCountFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
@Repository
public class TemplateCountFunctionDaoImpl implements TemplateCountFunctionDao {

    @Autowired
    private TemplateCountFunctionRepository templateCountFunctionRepository;

    @Override
    public List<TemplateCountFunction> findByRuleTemplate(Template template) {
        return templateCountFunctionRepository.findByTemplate(template);
    }

    @Override
    public TemplateCountFunction findById(Long templateCountFunctionId) {
        return templateCountFunctionRepository.findById(templateCountFunctionId).orElse(null);
    }

    @Override
    public Set<TemplateCountFunction> saveAll(List<TemplateCountFunction> templateCountFunctions) {
        Set<TemplateCountFunction> result = new HashSet<>();
        result.addAll(templateCountFunctionRepository.saveAll(templateCountFunctions));
        return result;
    }

    @Override
    public void deleteByTemplate(Template templateInDb) {
        templateCountFunctionRepository.deleteByTemplate(templateInDb);
    }
}
