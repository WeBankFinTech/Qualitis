package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateUdf;

import java.util.List;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface TemplateUdfDao {

    /**
     * Find template Udf Function by rule template
     * @param template
     * @return
     */
    List<TemplateUdf> findByRuleTemplate(Template template);

    /**
     * Find template Udf Function by id
     * @param templateUdfFunctionId
     * @return
     */
    TemplateUdf findById(Long templateUdfFunctionId);

    /**
     * Save all Udf Function .
     * @param templateUdfFunctions
     * @return
     */
    Set<TemplateUdf> saveAll(List<TemplateUdf> templateUdfFunctions);

    /**
     * Delete Udf Function by template
     * @param templateInDb
     */
    void deleteByTemplate(Template templateInDb);

}
