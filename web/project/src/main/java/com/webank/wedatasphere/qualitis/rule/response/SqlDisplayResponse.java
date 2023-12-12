/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.rule.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RegexpExprMapperRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateRegexpExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
public class SqlDisplayResponse {

    @JsonProperty("show_sql")
    private String showSql;
    private List<PlaceholderResponse> placeholders;

    public SqlDisplayResponse() {
    }

    public SqlDisplayResponse(String showSql) {
        this.showSql = showSql;
    }

    public SqlDisplayResponse(Template template, RegexpExprMapperRepository regexpExprMapperRepository) {
        placeholders = new ArrayList<>();
        showSql = template.getShowSql();
        for (TemplateMidTableInputMeta midTableInputMeta : template.getTemplateMidTableInputMetas()) {
            Integer inputType = midTableInputMeta.getInputType();
            if (inputType.equals(TemplateInputTypeEnum.REGEXP.getCode())) {
                // If inputType equals to Regexp, generate placeholder response
                Integer regexpType = midTableInputMeta.getRegexpType();
                List<TemplateRegexpExpr> templateRegexpExprs = regexpExprMapperRepository.findByRegexpType(regexpType);
                placeholders.add(new PlaceholderResponse(midTableInputMeta, templateRegexpExprs));
            } else {
                boolean flag = false;
                if (template.getName().equals(QualitisConstants.ROW_DATA_CONSISTENCY_VERIFICATION) &&
                        (inputType.equals(TemplateInputTypeEnum.AND_CONCAT.getCode()) || inputType.equals(TemplateInputTypeEnum.SOURCE_FIELDS.getCode()) ||
                                inputType.equals(TemplateInputTypeEnum.TARGET_FIELDS.getCode()))) {
                    flag = true;
                }

                if (flag) {
                    continue;
                } else {
                    placeholders.add(new PlaceholderResponse(midTableInputMeta));
                }

            }
        }

    }

    public String getShowSql() {
        return showSql;
    }

    public void setShowSql(String showSql) {
        this.showSql = showSql;
    }

    public List<PlaceholderResponse> getPlaceholders() {
        return placeholders;
    }

    public void setPlaceholders(List<PlaceholderResponse> placeholders) {
        this.placeholders = placeholders;
    }
}
