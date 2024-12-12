package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateCountFunction;

import java.util.List;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface TemplateCountFunctionDao {

    /**
     * Find template Count Function by rule template
     * @param template
     * @return
     */
    List<TemplateCountFunction> findByRuleTemplate(Template template);

    /**
     * Find template Count Function by id
     * @param templateCountFunctionId
     * @return
     */
    TemplateCountFunction findById(Long templateCountFunctionId);

    /**
     * Save all Count Function .
     * @param templateCountFunctions
     * @return
     */
    Set<TemplateCountFunction> saveAll(List<TemplateCountFunction> templateCountFunctions);

    /**
     * Delete Count Function by template
     * @param templateInDb
     */
    void deleteByTemplate(Template templateInDb);
}
