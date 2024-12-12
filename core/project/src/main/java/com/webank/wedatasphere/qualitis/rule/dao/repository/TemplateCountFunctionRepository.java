package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateCountFunction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface TemplateCountFunctionRepository extends JpaRepository<TemplateCountFunction, Long> {

    /**
     * Find template Udf Function by template
     * @param template
     * @return
     */
    List<TemplateCountFunction> findByTemplate(Template template);

    /**
     * Find template Udf Function by template and name
     * @param template
     * @param name
     * @return
     */
    TemplateCountFunction findByTemplateAndName(Template template, String name);

    /**
     * Delete TemplateUdfFunction Udf Function by template
     * @param templateInDb
     */
    void deleteByTemplate(Template templateInDb);
}
