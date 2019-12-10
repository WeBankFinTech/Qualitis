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

package com.webank.wedatasphere.qualitis.rule.adapter;

import com.google.common.collect.ImmutableMap;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.rule.constant.MappingOperationEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateRegexpTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RegexpExprMapperRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateRegexpExpr;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplateArgumentRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinConfigRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Component
public class AutoArgumentAdapter {

    private static final String[] SPECIAL_WORD_LIST = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};

    @Autowired
    private RegexpExprMapperRepository regexpExprMapperRepository;

    /**
     * Get auto adapt value from db
     * 1.Get input type from template
     * 2.If Type equals to fields, database, tables, list, condition, fixed_value, return values in request
     * 3.If Type equals to field_concat, return string that connected with comma
     * 4.If Type equals to regex, return values in request if replaceByRequest = true, else return value in database
     * @param templateMidTableInputMeta
     * @return
     */
    public Map<String, String> getAdaptValue(TemplateMidTableInputMeta templateMidTableInputMeta, DataSourceRequest dataSourceRequest,
                                             List<TemplateArgumentRequest> templateArgumentRequests) throws UnExpectedRequestException {
        Integer inputType = templateMidTableInputMeta.getInputType();
        if (inputType.equals(TemplateInputTypeEnum.CONDITION.getCode()) || inputType.equals(TemplateInputTypeEnum.LIST.getCode())
                || inputType.equals(TemplateInputTypeEnum.FIXED_VALUE.getCode())) {
            // Get value from request
            return ImmutableMap.of("value", findRequestById(templateMidTableInputMeta.getId(), templateArgumentRequests).getArgumentValue());
        } else if (inputType.equals(TemplateInputTypeEnum.REGEXP.getCode())) {
            // Get value from request if replaceByRequest = true, else return value in database
            return getRegexpValue(templateMidTableInputMeta, templateArgumentRequests);
        } else if (inputType.equals(TemplateInputTypeEnum.FIELD_CONCAT.getCode())) {
            // Get string that connected with comma
            return getFieldConcat(dataSourceRequest);
        } else {
            // Get value from request
            return findValueFromDataSourceRequest(inputType, dataSourceRequest);
        }
    }

    public Map<String, String> getMultiSourceAdaptValue(Rule rule, TemplateMidTableInputMeta templateMidTableInputMeta, String clusterName,
                                                        MultiDataSourceConfigRequest firstDataSource, MultiDataSourceConfigRequest secondDataSource,
                                                        List<MultiDataSourceJoinConfigRequest> mappings, String filter) throws UnExpectedRequestException {
        Integer inputType = templateMidTableInputMeta.getInputType();
        if (inputType.equals(TemplateInputTypeEnum.AND_CONCAT.getCode())) {
            // And_concat
            return ImmutableMap.of("value", generateAndConcatStatement(templateMidTableInputMeta, mappings));
        } else if (inputType.equals(TemplateInputTypeEnum.CONDITION.getCode())) {
            // Condition
            CommonChecker.checkString(filter, "Filter");
            return ImmutableMap.of("value", filter);
        }else {
            // Database, tables
            return findValueFromMultiDataSourceConfig(templateMidTableInputMeta, clusterName, firstDataSource, secondDataSource);
        }
    }

    private Map<String, String> findValueFromMultiDataSourceConfig(TemplateMidTableInputMeta templateMidTableInputMeta, String clusterName,
                                                                   MultiDataSourceConfigRequest firstDataSource, MultiDataSourceConfigRequest secondDataSource) {
        Map<String, String> map = new HashMap<>(4);
        Integer inputType = templateMidTableInputMeta.getInputType();
        if (inputType.equals(TemplateInputTypeEnum.SOURCE_DB.getCode())) {
            map.put("clusterName", clusterName);
            map.put("dbName", firstDataSource.getDbName());
            map.put("value", firstDataSource.getDbName());
        } else if (inputType.equals(TemplateInputTypeEnum.SOURCE_TABLE.getCode())) {
            map.put("clusterName", clusterName);
            map.put("dbName", firstDataSource.getDbName());
            map.put("tableName", firstDataSource.getTableName());
            map.put("value", firstDataSource.getTableName());
        } else if (inputType.equals(TemplateInputTypeEnum.TARGET_DB.getCode())) {
            map.put("clusterName", clusterName);
            map.put("dbName", secondDataSource.getDbName());
            map.put("value", secondDataSource.getDbName());
        } else if (inputType.equals(TemplateInputTypeEnum.TARGET_TABLE.getCode())) {
            map.put("clusterName", clusterName);
            map.put("dbName", secondDataSource.getDbName());
            map.put("tableName", secondDataSource.getTableName());
            map.put("value", secondDataSource.getTableName());
        }
        return map;
    }

    private String generateAndConcatStatement(TemplateMidTableInputMeta templateMidTableInputMeta, List<MultiDataSourceJoinConfigRequest> mappings) {
        String concatTemplate = templateMidTableInputMeta.getConcatTemplate();
        Collection<TemplateMidTableInputMeta> children = templateMidTableInputMeta.getChildren();

        List<String> originStatement = new ArrayList<>();
        for (MultiDataSourceJoinConfigRequest mapping : mappings) {
            String replacedTemplate = concatTemplate;
            Boolean addedFlag = false;
            for (TemplateMidTableInputMeta child : children) {
                if (child.getInputType().equals(TemplateInputTypeEnum.LEFT_STATEMENT.getCode())) {
                    replacedTemplate = replacedTemplate.replace(getPlaceHolder(child.getPlaceholder()), mapping.getLeftStatement());
                } else if (child.getInputType().equals(TemplateInputTypeEnum.OPERATION.getCode())) {
                    replacedTemplate = replacedTemplate.replace(getPlaceHolder(child.getPlaceholder()),
                            MappingOperationEnum.getByCode(mapping.getOperation()).getSymbol());
                } else if (child.getInputType().equals(TemplateInputTypeEnum.RIGHT_STATEMENT.getCode())) {
                    replacedTemplate = replacedTemplate.replace(getPlaceHolder(child.getPlaceholder()), mapping.getRightStatement());
                } else if (child.getInputType().equals(TemplateInputTypeEnum.SOURCE_FIELD.getCode())) {
                    for (MultiDataSourceJoinColumnRequest multiDataSourceJoinColumnRequest : mapping.getLeft()) {
                        String tmp1 = concatTemplate.replace(getPlaceHolder(child.getPlaceholder()), multiDataSourceJoinColumnRequest.getColumnName());
                        originStatement.add(tmp1);
                        addedFlag = true;
                    }
                } else if (child.getInputType().equals(TemplateInputTypeEnum.TARGET_FIELD.getCode())) {
                    for (MultiDataSourceJoinColumnRequest multiDataSourceJoinColumnRequest : mapping.getRight()) {
                        String tmp1 = concatTemplate.replace(getPlaceHolder(child.getPlaceholder()), multiDataSourceJoinColumnRequest.getColumnName());
                        originStatement.add(tmp1);
                        addedFlag = true;
                    }
                }
            }
            if (!addedFlag) {
                originStatement.add(replacedTemplate);
            }
        }

        return generateStatementByConcatStr(originStatement, "AND");
    }

    private String getPlaceHolder(String str) {
        return "${" + str + "}";
    }

    private String generateStatementByConcatStr(List<String> statement, String concatStr) {
        return String.join(" " + concatStr + " ", statement);
    }

    /**
     * 1.Get input type
     * 2.Auto adapt according to type
     */
    private Map<String, String> findValueFromDataSourceRequest(Integer inputType, DataSourceRequest request) throws UnExpectedRequestException {
        Map<String, String> map = new HashMap<>(4);
        if (inputType.equals(TemplateInputTypeEnum.FIELD.getCode())) {
            // Filed Type
            if (request.getColNames().size() != 1) {
                throw new UnExpectedRequestException("Can not find field from dataSource");
            }
            map.put("clusterName", request.getClusterName());
            map.put("dbName", request.getDbName());
            map.put("tableName", request.getTableName());
            map.put("value", request.getColNames().get(0).getColumnName());
        } else if (inputType.equals(TemplateInputTypeEnum.TABLE.getCode())) {
            // Table Type
            map.put("clusterName", request.getClusterName());
            map.put("dbName", request.getDbName());
            map.put("tableName", request.getTableName());
            map.put("value", request.getTableName());
        } else {
            // Database Type
            map.put("clusterName", request.getClusterName());
            map.put("dbName", request.getDbName());
            map.put("value", request.getDbName());
        }

        return Collections.unmodifiableMap(map);
    }

    private Map<String, String> getFieldConcat(DataSourceRequest dataSourceRequest) {
        return ImmutableMap.of("value", String.join(",", dataSourceRequest.getColNames().stream().map(DataSourceColumnRequest::getColumnName).collect(Collectors.toList())),
                "clusterName", dataSourceRequest.getClusterName(),
                "dbName", dataSourceRequest.getDbName(),
                "tableName", dataSourceRequest.getTableName());
    }

    /**
     * If replaceByRequest = true, return value in request
     * Else return value in database
     * @param templateMidTableInputMeta
     * @param templateArgumentRequests
     * @return
     */
    private Map<String, String> getRegexpValue(TemplateMidTableInputMeta templateMidTableInputMeta, List<TemplateArgumentRequest> templateArgumentRequests) throws UnExpectedRequestException {
        if (templateMidTableInputMeta.getReplaceByRequest()) {
            // Get value in request
            String regexp = findRequestById(templateMidTableInputMeta.getId(), templateArgumentRequests).getArgumentValue();
            String value = escapeExprSpecialWord(regexp);
            return ImmutableMap.of("value", value);
        } else {
            String key = null;
            // Get value in database
            if (templateMidTableInputMeta.getRegexpType().equals(TemplateRegexpTypeEnum.DATE.getCode())) {
                key = findRequestById(templateMidTableInputMeta.getId(), templateArgumentRequests).getArgumentValue();
            }
            TemplateRegexpExpr templateRegexpExpr = regexpExprMapperRepository.findByRegexpTypeAndKeyName(templateMidTableInputMeta.getRegexpType(), key);
            if (templateRegexpExpr == null) {
                throw new UnExpectedRequestException("KeyName: [" + key + "] is not supported");
            }

            String value = escapeExprSpecialWord(templateRegexpExpr.getRegexpValue());
            Map<String, String> map = new HashMap<>(2);
            map.put("value", value);
            map.put("originValue", key);
            return Collections.unmodifiableMap(map);
        }
    }

    private TemplateArgumentRequest findRequestById(Long id, List<TemplateArgumentRequest> templateArgumentRequests) throws UnExpectedRequestException {
        List<TemplateArgumentRequest> request = templateArgumentRequests.stream().filter(r -> r.getArgumentId().equals(id)).collect(Collectors.toList());
        if (request.size() != 1) {
            throw new UnExpectedRequestException("Can not find suitable request");
        }
        TemplateArgumentRequest.checkRequest(request.get(0));

        return request.get(0);
    }

    private String escapeExprSpecialWord(String str) {
        if (StringUtils.isNotBlank(str)) {
            for (String specialWord : SPECIAL_WORD_LIST) {
                if (str.contains(specialWord)) {
                    str = str.replace(specialWord, "\\" + specialWord);
                }
            }
        }
        return str;
    }

}
