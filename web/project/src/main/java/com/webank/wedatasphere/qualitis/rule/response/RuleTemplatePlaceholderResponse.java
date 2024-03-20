package com.webank.wedatasphere.qualitis.rule.response;

import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RegexpExprMapperRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateDefaultInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateRegexpExpr;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public class RuleTemplatePlaceholderResponse {

    private List<PlaceholderResponse> placeholders;

    public RuleTemplatePlaceholderResponse() {
    }

    public RuleTemplatePlaceholderResponse(Template template) {
        placeholders = new ArrayList<>();
        for (TemplateMidTableInputMeta midTableInputMeta : template.getTemplateMidTableInputMetas()) {
            Integer inputType = midTableInputMeta.getInputType();
            if (inputType.equals(TemplateInputTypeEnum.REGEXP.getCode())) {
                // If inputType equals to Regexp, generate placeholder response
                Integer regexpType = midTableInputMeta.getRegexpType();
                List<TemplateRegexpExpr> templateRegexpExprs = SpringContextHolder.getBean(RegexpExprMapperRepository.class).findByRegexpType(regexpType);
                placeholders.add(new PlaceholderResponse(midTableInputMeta, templateRegexpExprs));
            } else {
                placeholders.add(new PlaceholderResponse(midTableInputMeta));
            }
        }
    }

//    public RuleTemplatePlaceholderResponse(List<TemplateMidTableInputMeta> templateMidTableInputMetas) {
//        placeholders = new ArrayList<>();
//        for (TemplateMidTableInputMeta midTableInputMeta : templateMidTableInputMetas) {
//            Integer inputType = midTableInputMeta.getInputType();
//            if (inputType.equals(TemplateInputTypeEnum.REGEXP.getCode())) {
//                // If inputType equals to Regexp, generate placeholder response
//                Integer regexpType = midTableInputMeta.getRegexpType();
//                List<TemplateRegexpExpr> templateRegexpExprs = SpringContextHolder.getBean(RegexpExprMapperRepository.class).findByRegexpType(regexpType);
//                placeholders.add(new PlaceholderResponse(midTableInputMeta, templateRegexpExprs));
//            } else {
//                placeholders.add(new PlaceholderResponse(midTableInputMeta));
//            }
//        }
//    }

    public RuleTemplatePlaceholderResponse(List<TemplateDefaultInputMeta> templateDefaultInputMeta) {
        placeholders = new ArrayList<>();
        for (TemplateDefaultInputMeta defaultInputMeta : templateDefaultInputMeta) {
            placeholders.add(new PlaceholderResponse(defaultInputMeta));
        }
    }


    public List<PlaceholderResponse> getPlaceholders() {
        return placeholders;
    }

    public void setPlaceholders(List<PlaceholderResponse> placeholders) {
        this.placeholders = placeholders;
    }

}
