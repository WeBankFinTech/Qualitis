package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.TemplateUserDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.TemplateUserRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author allenzhou
 */
@Repository
public class TemplateUserDaoImpl implements TemplateUserDao {
    @Autowired
    private TemplateUserRepository templateUserRepository;

    @Override
    public TemplateUser saveTemplateUser(TemplateUser templateUser) {
        return templateUserRepository.save(templateUser);
    }

    @Override
    public void delete(TemplateUser templateUser) {
        templateUserRepository.delete(templateUser);
    }

    @Override
    public TemplateUser findByTemplate(Template templateInDb) {
        return templateUserRepository.findBytemplate(templateInDb);
    }
}
