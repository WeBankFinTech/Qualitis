package com.webank.wedatasphere.qualitis.rule.util;

import com.webank.wedatasphere.qualitis.rule.dao.RuleVariableDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.TemplateStatisticsInputMetaRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateMidTableInputMetaDao;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author allenzhou@webank.com
 * @date 2022/12/20 15:45
 */
public class LazyGetUtil {

    public static Set<TemplateMidTableInputMeta> getTemplateMidTableInputMetas(Template template) {
        List<TemplateMidTableInputMeta> templateMidTableInputMetaList = SpringContextHolder.getBean(TemplateMidTableInputMetaDao.class).findByRuleTemplate(template);
        Set<TemplateMidTableInputMeta> templateMidTableInputMetaSet = new HashSet<>();
        templateMidTableInputMetaSet.addAll(templateMidTableInputMetaList);
        return templateMidTableInputMetaSet;
    }

    public static Set<TemplateStatisticsInputMeta> getStatisticAction(Template template) {
        List<TemplateStatisticsInputMeta> templateStatisticsInputMetas = SpringContextHolder.getBean(TemplateStatisticsInputMetaRepository.class).findByTemplate(template);
        Set<TemplateStatisticsInputMeta> templateStatisticsInputMetaSet = new HashSet<>();
        templateStatisticsInputMetaSet.addAll(templateStatisticsInputMetas);
        return templateStatisticsInputMetaSet;
    }

    public static Set<RuleVariable> getRuleVariables(Rule rule) {
        List<RuleVariable> ruleVariables = SpringContextHolder.getBean(RuleVariableDao.class).findByRule(rule);
        Set<RuleVariable> ruleVariableHashSet = new HashSet<>();
        ruleVariableHashSet.addAll(ruleVariables);
        return ruleVariableHashSet;
    }
}
