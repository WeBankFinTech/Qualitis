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

package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.DepartmentDao;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.dto.DataVisibilityPermissionDto;
import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserRole;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.parser.LocaleParser;
import com.webank.wedatasphere.qualitis.request.DepartmentSubInfoRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.DepartmentSubInfoResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.constant.FieldTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.FunctionTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.NamingMethodEnum;
import com.webank.wedatasphere.qualitis.rule.constant.RoleSystemTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTemplateTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.StatisticalFunctionEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TableDataTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateActionTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateCheckLevelEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateCheckTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateDataSourceTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateFileTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.NamingConventionsDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateCountFunctionDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateDataSourceTypeDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateDepartmentDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateMidTableInputMetaDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateOutputMetaDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateUdfDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateUserDao;
import com.webank.wedatasphere.qualitis.rule.dao.UserRuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RegexpExprMapperRepository;
import com.webank.wedatasphere.qualitis.rule.entity.DataVisibility;
import com.webank.wedatasphere.qualitis.rule.entity.NamingConventions;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateDataSourceType;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateDefaultInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateUdf;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateUser;
import com.webank.wedatasphere.qualitis.rule.request.AddCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddFileRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleTemplateRequest;
import com.webank.wedatasphere.qualitis.rule.request.CustomAlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleTemplateRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplateMidTableInputMetaRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplatePageRequest;
import com.webank.wedatasphere.qualitis.rule.response.NamingConventionsResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleTemplatePlaceholderResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleTemplateResponse;
import com.webank.wedatasphere.qualitis.rule.response.TemplateInputDemandResponse;
import com.webank.wedatasphere.qualitis.rule.response.TemplateMetaResponse;
import com.webank.wedatasphere.qualitis.rule.response.TemplateMidTableInputMetaResponse;
import com.webank.wedatasphere.qualitis.rule.response.TemplateOutputMetaResponse;
import com.webank.wedatasphere.qualitis.rule.response.TemplateStatisticsInputMetaResponse;
import com.webank.wedatasphere.qualitis.rule.service.RuleService;
import com.webank.wedatasphere.qualitis.rule.service.RuleTemplateService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateMidTableInputMetaService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateOutputMetaService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateStatisticsInputMetaService;
import com.webank.wedatasphere.qualitis.scheduled.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.service.DataVisibilityService;
import com.webank.wedatasphere.qualitis.service.RoleService;
import com.webank.wedatasphere.qualitis.service.RuleMetricCommonService;
import com.webank.wedatasphere.qualitis.service.SubDepartmentPermissionService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Service
public class RuleTemplateServiceImpl implements RuleTemplateService {
    @Autowired
    private RuleMetricDao ruleMetricDao;
    @Autowired
    private RuleTemplateDao ruleTemplateDao;
    @Autowired
    private TemplateMidTableInputMetaDao templateMidTableInputMetaDao;

    @Autowired
    private TemplateOutputMetaDao templateOutputMetaDao;

    @Autowired
    private UserRuleTemplateDao userRuleTemplateDao;

    @Autowired
    private RegexpExprMapperRepository regexpExprMapperRepository;

    @Autowired
    private TemplateStatisticsInputMetaService templateStatisticsInputMetaService;

    @Autowired
    private TemplateMidTableInputMetaService templateMidTableInputMetaService;

    @Autowired
    private TemplateOutputMetaService templateOutputMetaService;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RuleDao ruleDao;

    @Autowired
    private TemplateDepartmentDao templateDepartmentDao;

    @Autowired
    private TemplateDataSourceTypeDao templateDataSourceTypeDao;

    @Autowired
    private TemplateUserDao templateUserDao;
    @Autowired
    private DataVisibilityService dataVisibilityService;
    @Autowired
    private SubDepartmentPermissionService subDepartmentPermissionService;
    @Autowired
    private TemplateUdfDao templateUdfDao;
    @Autowired
    private TemplateCountFunctionDao templateCountFunctionDao;
    @Autowired
    private LocaleParser localeParser;
    @Autowired
    private NamingConventionsDao namingConventionsDao;
    @Autowired
    private RuleMetricCommonService ruleMetricCommonService;

    private final Long ALL_DEPARTMENT_VISIBILITY = 0L;

    private HttpServletRequest httpServletRequest;
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleTemplateServiceImpl.class);
    public static final FastDateFormat PRINT_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    public RuleTemplateServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public GeneralResponse<GetAllResponse<RuleTemplateResponse>> getCustomRuleTemplateByUser(PageRequest request) throws UnExpectedRequestException {
        // Check Arguments
        PageRequest.checkRequest(request);

        // Get userId
        Long userId = HttpUtils.getUserId(httpServletRequest);

        int size = request.getSize();
        int page = request.getPage();
        List<Template> templates = userRuleTemplateDao.findByUserId(userId, page, size);
        long total = userRuleTemplateDao.countByUserId(userId);
        GetAllResponse<RuleTemplateResponse> response = new GetAllResponse<>();
        response.setTotal(total);
        List<RuleTemplateResponse> responseList = new ArrayList<>();
        for (Template template : templates) {
            RuleTemplateResponse ruleTemplateResponse = new RuleTemplateResponse(template, false);
            List<Integer> types = templateDataSourceTypeDao.findByTemplate(template).stream().map(TemplateDataSourceType::getDataSourceTypeId).collect(
                    Collectors.toList());
            ruleTemplateResponse.setDatasourceType(types);
            responseList.add(ruleTemplateResponse);
        }
        response.setData(responseList);

        LOGGER.info("Succeed to get custom rule_template. response: {}", response);
        return new GeneralResponse<>("200", "{&GET_CUSTOM_RULE_TEMPLATE_SUCCESSFULLY}", response);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public Template addCustomTemplate(AddCustomRuleRequest request, String loginUser) throws UnExpectedRequestException, IOException, PermissionDeniedRequestException {
        Template newTemplate = new Template();
        newTemplate.setName(request.getProjectId() + "_" + request.getRuleName() + "_template");
        newTemplate.setSaveMidTable(request.getSaveMidTable());
        String sqlCheckArea = request.getSqlCheckArea();
        if (StringUtils.isNotBlank(sqlCheckArea)) {
            newTemplate.setMidTableAction(sqlCheckArea);
            newTemplate.setTemplateType(RuleTemplateTypeEnum.CUSTOM.getCode());
            newTemplate.setActionType(TemplateActionTypeEnum.SQL.getCode());
            Template savedTemplate = ruleTemplateDao.saveTemplate(newTemplate);
            LOGGER.info("Succeed to save custom template, template_id: {}", savedTemplate.getId());
            // Generate statistics input meta by rule metric
            //处理自动创建的指标 并赋值给RuleMetricEnCode
            for (CustomAlarmConfigRequest customAlarmConfigRequest : request.getAlarmVariable()) {

                if (StringUtils.isNotBlank(customAlarmConfigRequest.getRuleMetricName())) {
                    Boolean flag = false;
                    if (CollectionUtils.isNotEmpty(request.getDataSourceEnvRequests()) || CollectionUtils.isNotEmpty(request.getDataSourceEnvMappingRequests())) {
                        flag = true;
                    }

                    RuleMetric ruleMetric = ruleMetricCommonService.accordingRuleMetricNameAdd(customAlarmConfigRequest.getRuleMetricName(), loginUser, flag);
                    if (ruleMetric == null) {
                        throw new UnExpectedRequestException("{&FAILED_TO_AUTOMATE_CREATE_METRICS}");
                    } else {
                        customAlarmConfigRequest.setRuleMetricEnCode(ruleMetric.getEnCode());
                    }

                }
            }

            Set<String> ruleMetricEnCodeSet = request.getAlarmVariable().stream().map(CustomAlarmConfigRequest::getRuleMetricEnCode).collect(Collectors.toSet());
            Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas = new HashSet<>(ruleMetricEnCodeSet.size());
            Set<TemplateOutputMeta> templateOutputMetas = new HashSet<>(ruleMetricEnCodeSet.size());
            for (String enCode : ruleMetricEnCodeSet) {
                RuleMetric ruleMetricInDb = ruleMetricDao.findByEnCode(enCode);
                templateStatisticsInputMetas.addAll(templateStatisticsInputMetaService.getAndSaveTemplateStatisticsInputMeta(
                        ruleMetricInDb.getName(), FunctionTypeEnum.SUM_FUNCTION.getCode(), ruleMetricInDb.getName(), true, savedTemplate));
                templateOutputMetas.addAll(templateOutputMetaService.getAndSaveTemplateOutputMeta(ruleMetricInDb.getName(),
                        FunctionTypeEnum.SUM_FUNCTION.getCode(), true, savedTemplate, null));
            }
            savedTemplate.setStatisticAction(templateStatisticsInputMetas);
            savedTemplate.setTemplateOutputMetas(templateOutputMetas);
            return savedTemplate;
        } else {
            newTemplate.setMidTableAction(getMidTableAction(request));
            newTemplate.setTemplateType(RuleTemplateTypeEnum.CUSTOM.getCode());
            newTemplate.setActionType(TemplateActionTypeEnum.SQL.getCode());
            Template savedTemplate = ruleTemplateDao.saveTemplate(newTemplate);
            LOGGER.info("Succeed to save custom template, template_id: {}", savedTemplate.getId());

            Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas = templateStatisticsInputMetaService.getAndSaveTemplateStatisticsInputMeta(
                    request.getOutputName(), request.getFunctionType(), request.getFunctionContent(), request.getSaveMidTable(), savedTemplate);
            savedTemplate.setStatisticAction(templateStatisticsInputMetas);

            Set<TemplateOutputMeta> templateOutputMetas = templateOutputMetaService.getAndSaveTemplateOutputMeta(request.getOutputName(),
                    request.getFunctionType(), request.getSaveMidTable(), savedTemplate, null);
            savedTemplate.setTemplateOutputMetas(templateOutputMetas);
            return savedTemplate;
        }
    }

    @Override
    public Template modifyCustomTemplate(AddCustomRuleRequest request, Template template, String loginUser) throws UnExpectedRequestException, IOException, PermissionDeniedRequestException {
        LOGGER.info("Template start to clear.");
        templateOutputMetaDao.deleteByTemplate(template);
        templateDataSourceTypeDao.deleteByTemplate(template);
        templateStatisticsInputMetaService.deleteByTemplate(template);
        template.setName(request.getProjectId() + "_" + request.getRuleName() + "_template");
        template.setSaveMidTable(request.getSaveMidTable());
        String sqlCheckArea = request.getSqlCheckArea();
        if (StringUtils.isNotBlank(sqlCheckArea)) {
            template.setMidTableAction(sqlCheckArea);
            LOGGER.info("Template start to modify entity.");
            Template savedTemplate = ruleTemplateDao.saveTemplate(template);
            LOGGER.info("Succeed to modify custom template, template id: {}", savedTemplate.getId());
            // Generate statistics input meta by rule metric
            //处理自动创建的指标 并赋值给RuleMetricEnCode
            for (CustomAlarmConfigRequest customAlarmConfigRequest : request.getAlarmVariable()) {

                if (StringUtils.isNotBlank(customAlarmConfigRequest.getRuleMetricName())) {
                    Boolean flag = false;
                    if (CollectionUtils.isNotEmpty(request.getDataSourceEnvRequests()) || CollectionUtils.isNotEmpty(request.getDataSourceEnvMappingRequests())) {
                        flag = true;
                    }
                    RuleMetric ruleMetric = ruleMetricCommonService.accordingRuleMetricNameAdd(customAlarmConfigRequest.getRuleMetricName(), loginUser, flag);
                    if (ruleMetric == null) {
                        throw new UnExpectedRequestException("{&FAILED_TO_AUTOMATE_CREATE_METRICS}");
                    } else {
                        customAlarmConfigRequest.setRuleMetricEnCode(ruleMetric.getEnCode());
                    }

                }
            }

            Set<String> ruleMetricEnCodeSet = request.getAlarmVariable().stream().map(CustomAlarmConfigRequest::getRuleMetricEnCode).collect(Collectors.toSet());
            Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas = new HashSet<>(ruleMetricEnCodeSet.size());
            Set<TemplateOutputMeta> templateOutputMetas = new HashSet<>(ruleMetricEnCodeSet.size());
            for (String enCode : ruleMetricEnCodeSet) {
                RuleMetric ruleMetricInDb = ruleMetricDao.findByEnCode(enCode);
                templateStatisticsInputMetas.addAll(templateStatisticsInputMetaService.getAndSaveTemplateStatisticsInputMeta(
                        ruleMetricInDb.getName(), FunctionTypeEnum.SUM_FUNCTION.getCode(), ruleMetricInDb.getName(), true, savedTemplate));
                templateOutputMetas.addAll(templateOutputMetaService.getAndSaveTemplateOutputMeta(ruleMetricInDb.getName(),
                        FunctionTypeEnum.SUM_FUNCTION.getCode(), true, savedTemplate, null));
            }
            savedTemplate.setStatisticAction(templateStatisticsInputMetas);
            savedTemplate.setTemplateOutputMetas(templateOutputMetas);
            return savedTemplate;
        } else {
            template.setMidTableAction(getMidTableAction(request));
            Template savedTemplate = ruleTemplateDao.saveTemplate(template);
            LOGGER.info("Succeed to modify custom template, template id: {}", savedTemplate.getId());

            Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas = templateStatisticsInputMetaService.getAndSaveTemplateStatisticsInputMeta(
                    request.getOutputName(), request.getFunctionType(), request.getFunctionContent(), request.getSaveMidTable(), savedTemplate);
            savedTemplate.setStatisticAction(templateStatisticsInputMetas);

            Set<TemplateOutputMeta> templateOutputMetas = templateOutputMetaService.getAndSaveTemplateOutputMeta(request.getOutputName(),
                    request.getFunctionType(), request.getSaveMidTable(), savedTemplate, null);
            savedTemplate.setTemplateOutputMetas(templateOutputMetas);
            return savedTemplate;
        }
    }

    @Override
    public void deleteCustomTemplate(Template template) throws UnExpectedRequestException {
        if (template == null) {
            throw new UnExpectedRequestException("Template {&DOES_NOT_EXIST}");
        }
        if (!RuleTemplateTypeEnum.CUSTOM.getCode().equals(template.getTemplateType())) {
            throw new UnExpectedRequestException("Template(id:[" + template.getId() + "]) {&IS_NOT_A_CUSTOM_TEMPLATE}");
        }
        List<TemplateDataSourceType> templateDataSourceTypes = templateDataSourceTypeDao.findByTemplate(template);
        for (TemplateDataSourceType templateDataSourceType : templateDataSourceTypes) {
            templateDataSourceTypeDao.delete(templateDataSourceType);
        }
        ruleTemplateDao.deleteTemplate(template);
    }

    @Override
    public GeneralResponse<GetAllResponse<RuleTemplateResponse>> getDefaultRuleTemplate(TemplatePageRequest request) throws UnExpectedRequestException {
        String dataSourceType = request.getDataSourceType();
        Integer dataSourceTypeCode = TemplateDataSourceTypeEnum.getCode(dataSourceType);

        List<Template> templates;
        long total = 0;
        User userInDb;
        if (StringUtils.isNotBlank(request.getUserName())) {
            userInDb = userDao.findByUsername(request.getUserName());
        } else {
            userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
        }
        if (userInDb == null) {
            throw new UnExpectedRequestException("User name {&REQUEST_CAN_NOT_BE_NULL}");
        }

        Set<String> actionRangeSet = CollectionUtils.isEmpty(request.getActionRange()) ? null : request.getActionRange();

        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        Integer roleType = roleService.getRoleType(userRoles);
        if (roleType.equals(RoleSystemTypeEnum.ADMIN.getCode())) {
            templates = ruleTemplateDao.findAllDefaultTemplate(request.getPage(), request.getSize(), request.getTemplateType(), request.getCnName(), request.getEnName(), dataSourceTypeCode, request.getVerificationLevel(), request.getVerificationType(), request.getCreateName(), request.getModifyName(), request.getDevDepartmentId(), request.getOpsDepartmentId(), actionRangeSet, TableDataTypeEnum.RULE_TEMPLATE.getCode());
            total = ruleTemplateDao.countAllDefaultTemplate(request.getTemplateType(), request.getCnName(), request.getEnName(), dataSourceTypeCode, request.getVerificationLevel(), request.getVerificationType(), request.getCreateName(), request.getModifyName(), request.getDevDepartmentId(), request.getOpsDepartmentId(), actionRangeSet, TableDataTypeEnum.RULE_TEMPLATE.getCode());

        } else if (roleType.equals(RoleSystemTypeEnum.DEPARTMENT_ADMIN.getCode())) {
            List<Long> departmentIds = userRoles.stream().map(UserRole::getRole).filter(Objects::nonNull)
                    .map(Role::getDepartment).filter(Objects::nonNull)
                    .map(Department::getId).collect(Collectors.toList());
            if (Objects.nonNull(userInDb.getDepartment())) {
                departmentIds.add(userInDb.getDepartment().getId());
            }
            List<Long> devAndOpsInfoWithDeptList = subDepartmentPermissionService.getSubDepartmentIdList(departmentIds);
            Page<Template> resultPage = ruleTemplateDao.findTemplates(request.getTemplateType(), dataSourceTypeCode, TableDataTypeEnum.RULE_TEMPLATE.getCode(), devAndOpsInfoWithDeptList.isEmpty() ? null : devAndOpsInfoWithDeptList, userInDb.getId(), request.getCnName(), request.getEnName(), request.getVerificationLevel(), request.getVerificationType(), request.getCreateName(), request.getModifyName(), request.getDevDepartmentId(), request.getOpsDepartmentId(),actionRangeSet, request.getPage(), request.getSize());
            templates = resultPage.getContent();
            total = resultPage.getTotalElements();
        } else {
            Page<Template> resultPage = ruleTemplateDao.findTemplates(request.getTemplateType(), dataSourceTypeCode, TableDataTypeEnum.RULE_TEMPLATE.getCode(), Arrays.asList(userInDb.getSubDepartmentCode()), userInDb.getId(), request.getCnName(), request.getEnName(), request.getVerificationLevel(), request.getVerificationType(), request.getCreateName(), request.getModifyName(), request.getDevDepartmentId(), request.getOpsDepartmentId(),actionRangeSet, request.getPage(), request.getSize());
            templates = resultPage.getContent();
            total = resultPage.getTotalElements();
        }

        List<RuleTemplateResponse> responseList = templates.stream().map(RuleTemplateResponse::new).collect(Collectors.toList());
        setDatasourceTypeToResp(responseList);
        setDataVisibilityToResp(responseList);

        GetAllResponse<RuleTemplateResponse> response = new GetAllResponse<>(total, responseList);

        LOGGER.info("Succeed to find default rule_template. response: {}", response);
        return new GeneralResponse<>("200", "{&GET_RULE_TEMPLATE_SUCCESSFULLY}", response);
    }

    private void setDatasourceTypeToResp(List<RuleTemplateResponse> responseList) {
        List<Long> templateIds = responseList.stream().map(RuleTemplateResponse::getRuleTemplateId).collect(Collectors.toList());
        List<TemplateDataSourceType> templateDataSourceTypes = templateDataSourceTypeDao.findByTemplateIds(templateIds);
        if (CollectionUtils.isEmpty(templateDataSourceTypes)) {
            return;
        }
        Map<Long, List<TemplateDataSourceType>> dataVisibilityMap = templateDataSourceTypes.stream().collect(Collectors.groupingBy(templateDataSourceType -> templateDataSourceType.getTemplate().getId()));

        for (RuleTemplateResponse ruleTemplateResponse : responseList) {
            List<TemplateDataSourceType> templateDataSourceTypeList = dataVisibilityMap.get(ruleTemplateResponse.getRuleTemplateId());
            if (CollectionUtils.isNotEmpty(templateDataSourceTypeList)) {
                List<Integer> types = templateDataSourceTypeList.stream().map(TemplateDataSourceType::getDataSourceTypeId).collect(Collectors.toList());
                ruleTemplateResponse.setDatasourceType(types);
            }
        }
    }

    private void setDataVisibilityToResp(List<RuleTemplateResponse> responseList) {
        List<Long> templateIds = responseList.stream().map(RuleTemplateResponse::getRuleTemplateId).collect(Collectors.toList());
        List<DataVisibility> dataVisibilityList = dataVisibilityService.filterByIds(templateIds, TableDataTypeEnum.RULE_TEMPLATE);
        if (CollectionUtils.isEmpty(dataVisibilityList)) {
            return;
        }
        Map<Long, List<DataVisibility>> dataVisibilityMap = dataVisibilityList.stream().collect(Collectors.groupingBy(DataVisibility::getTableDataId));

        for (RuleTemplateResponse ruleTemplateResponse : responseList) {
            List<DataVisibility> templateDtaVisibilityList = dataVisibilityMap.get(ruleTemplateResponse.getRuleTemplateId());
            if (CollectionUtils.isNotEmpty(templateDtaVisibilityList)) {
                List<DepartmentSubInfoResponse> departmentInfoResponses = templateDtaVisibilityList.stream().map(DepartmentSubInfoResponse::new).collect(Collectors.toList());
                ruleTemplateResponse.setVisibilityDepartmentNameList(departmentInfoResponses);
            }
        }
    }

    @Override
    public GeneralResponse<TemplateMetaResponse> getRuleTemplateMeta(Long ruleTemplateId) throws UnExpectedRequestException {
        // Find rule template by id
        Template templateInDb = ruleTemplateDao.findById(ruleTemplateId);
        if (null == templateInDb) {
            throw new UnExpectedRequestException("rule_template_id {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }

        // Find input meta data by template
        List<TemplateOutputMeta> templateOutputMetas = templateOutputMetaDao.findByRuleTemplate(templateInDb);
        List<Integer> types = templateDataSourceTypeDao.findByTemplate(templateInDb).stream().map(TemplateDataSourceType::getDataSourceTypeId).collect(
                Collectors.toList());
        TemplateMetaResponse response = new TemplateMetaResponse(templateInDb, templateOutputMetas, types);

        LOGGER.info("Succeed to get rule_template. rule_template_id: {}", ruleTemplateId);
        return new GeneralResponse<>("200", "{&GET_RULE_TEMPLATE_META_SUCCESSFULLY}", response);
    }

    @Override
    public GeneralResponse<TemplateInputDemandResponse> getRuleTemplateInputMeta(Long ruleTemplateId) throws UnExpectedRequestException {
        // Check existence of rule template
        Template templateInDb = ruleTemplateDao.findById(ruleTemplateId);
        if (null == templateInDb) {
            throw new UnExpectedRequestException("rule_template_id {&DOES_NOT_EXIST}");
        }

        TemplateInputDemandResponse templateInputDemandResponse = new TemplateInputDemandResponse(templateInDb, regexpExprMapperRepository);
        LOGGER.info("Succeed to get the input of rule_template. rule_template_id: {}", ruleTemplateId);
        return new GeneralResponse<>("200", "{&GET_TEMPLATE_RULE_DEMAND_SUCCESSFULLY}", templateInputDemandResponse);
    }

    @Override
    public Template checkRuleTemplate(Long ruleTemplateId) throws UnExpectedRequestException {
        Template template = ruleTemplateDao.findById(ruleTemplateId);
        if (template == null) {
            throw new UnExpectedRequestException("Rule template {&DOES_NOT_EXIST}");
        }
        return template;
    }

    private String getMidTableAction(AddCustomRuleRequest request) {
        StringBuilder sb = new StringBuilder("SELECT ");
        sb.append(request.getFunctionContent());
        sb.append(" FROM ").append(request.getFromContent()).append(" WHERE ").append("${filter}");
        // Check SQL grammar
        String sql = sb.toString();
        return sql;
    }

    @Override
    public String getFunctionAlias(Integer code) {
        return "my" + FunctionTypeEnum.getByCode(code).getFunction();
    }

    @Override
    public GeneralResponse<TemplateInputDemandResponse> getMultiSourceTemplateMeta(Long templateId) throws UnExpectedRequestException {
        // Check existence of rule template
        Template templateInDb = checkRuleTemplate(templateId);
        if (templateInDb.getTemplateType().equals(RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode()) || templateInDb.getTemplateType().equals(RuleTemplateTypeEnum.FILE_COUSTOM.getCode())) {
            // return show_sql of template and template output
            TemplateInputDemandResponse templateInputDemandResponse = new TemplateInputDemandResponse(templateInDb, templateInDb.getTemplateType());
            LOGGER.info("Succeed to get the input of rule_template. rule_template_id: {}", templateId);
            return new GeneralResponse<>("200", "{&GET_TEMPLATE_RULE_DEMAND_SUCCESSFULLY}", templateInputDemandResponse);
        } else {
            throw new UnExpectedRequestException("Template : [" + templateId + "] {&IS_NOT_A_MULTI_OR_A_FILE_TEMPLATE}");
        }
    }

    /**
     * Paging get template
     *
     * @param request
     * @return
     */
    @Override
    public GeneralResponse<GetAllResponse<RuleTemplateResponse>> getMultiRuleTemplate(TemplatePageRequest request) throws UnExpectedRequestException {
        // Check Arguments
//        TemplatePageRequest.checkRequest(request);
//
//        int size = request.getSize();
//        int page = request.getPage();
//        Integer dataSourceTypeCode = StringUtils.isNotBlank(request.getDataSourceType()) ? TemplateDataSourceTypeEnum.getCode(request.getDataSourceType()) : null;
//
//        String cnName = null, enName = null;
//        if (StringUtils.isNotBlank(request.getCnName())) {
//            cnName = SpecCharEnum.PERCENT.getValue() + request.getCnName() + SpecCharEnum.PERCENT.getValue();
//        } else {
//            cnName = "";
//        }
//        if (StringUtils.isNotBlank(request.getEnName())) {
//            enName = SpecCharEnum.PERCENT.getValue() + request.getEnName() + SpecCharEnum.PERCENT.getValue();
//        } else {
//            enName = "";
//        }
//
//        if (StringUtils.isNotBlank(request.getCreateName())) {
//            User user = userDao.findByUsername(request.getCreateName());
//            if (user != null) {
//                request.setCreateName(user.getId() + "");
//            } else {
//                request.setCreateName(null);
//            }
//
//        }
//        if (StringUtils.isNotBlank(request.getModifyName())) {
//            User user = userDao.findByUsername(request.getModifyName());
//            if (user != null) {
//                request.setModifyName(user.getId() + "");
//            } else {
//                request.setModifyName(null);
//            }
//        }
//
//        List<User> users = new ArrayList<>(1);
//        List<Department> departments = new ArrayList<>(1);
//
//        List<Template> templates;
//        long total = 0;
//        User userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
//        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
//        Integer roleType = roleService.getRoleType(userRoles);
//        if (roleType.equals(RoleSystemTypeEnum.ADMIN.getCode())) {
//            templates = ruleTemplateDao.findAllDefaultTemplate(page, size, RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode(), cnName, enName, dataSourceTypeCode, request.getVerificationLevel(), request.getVerificationType(), request.getCreateName(), request.getModifyName(), request.getDevDepartmentId(), request.getOpsDepartmentId());
//            total = ruleTemplateDao.countAllDefaultTemplate(RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode(), cnName, enName, dataSourceTypeCode, request.getVerificationLevel(), request.getVerificationType(), request.getCreateName(), request.getModifyName(), request.getDevDepartmentId(), request.getOpsDepartmentId());
//
//        } else if (roleType.equals(RoleSystemTypeEnum.DEPARTMENT_ADMIN.getCode())) {
//            for (UserRole userRole : userRoles) {
//                Department department = userRole.getRole().getDepartment();
//                if (department != null) {
//                    departments.add(department);
//                    List<User> userList = userDao.findByDepartment(department);
//                    users.addAll(userList);
//                }
//            }
//
//            departments.add(userInDb.getDepartment());
//            List<Long> departmentIds = departments.stream().filter(Objects::nonNull).map(Department::getId).collect(Collectors.toList());
//            List<Long> devAndOpsInfoWithDeptList = subDepartmentPermissionService.getSubDepartmentIdList(departmentIds);
//            Page<Template> resultPage = ruleTemplateDao.findTemplates(RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode(), dataSourceTypeCode, TableDataTypeEnum.RULE_TEMPLATE.getCode(), devAndOpsInfoWithDeptList.isEmpty() ? null : devAndOpsInfoWithDeptList, userInDb, cnName, enName, request.getVerificationLevel(), request.getVerificationType(), request.getCreateName(), request.getModifyName(), request.getDevDepartmentId(), request.getOpsDepartmentId(), page, size);
//            templates = resultPage.getContent();
//            total = resultPage.getTotalElements();
//        } else {
//            Department department = userInDb.getDepartment();
//            if (department != null) {
//                departments.add(department);
//            }
//            users.add(userInDb);
//            Page<Template> resultPage = ruleTemplateDao.findTemplates(RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode(), dataSourceTypeCode, TableDataTypeEnum.RULE_TEMPLATE.getCode(), Arrays.asList(userInDb.getSubDepartmentCode()), userInDb, cnName, enName, request.getVerificationLevel(), request.getVerificationType(), request.getCreateName(), request.getModifyName(), request.getDevDepartmentId(), request.getOpsDepartmentId(), page, size);
//            templates = resultPage.getContent();
//            total = resultPage.getTotalElements();
//        }
//
//        List<RuleTemplateResponse> responseList = templates.stream().map(RuleTemplateResponse::new).collect(Collectors.toList());
//        setDatasourceTypeToResp(responseList);
//        setDataVisibilityToResp(responseList);
//
//        GetAllResponse<RuleTemplateResponse> response = new GetAllResponse<>(total, responseList);
//
//        LOGGER.info("Succeed to find multi rule_template. response: {}", response);
        return new GeneralResponse<>("200", "{&GET_MULTI_RULE_TEMPLATE_SUCCESSFULLY}", null);
    }

    /**
     * Get meta data information by template_id
     *
     * @param ruleTemplateId
     * @return
     * @throws UnExpectedRequestException
     */
    @Override
    public GeneralResponse<TemplateMetaResponse> getRuleMultiTemplateMeta(Long ruleTemplateId) throws UnExpectedRequestException {
        // Find rule template by rule template id
        Template templateInDb = ruleTemplateDao.findById(ruleTemplateId);
        if (null == templateInDb) {
            throw new UnExpectedRequestException("rule_template_id {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }

        // Find meta data of template
        List<TemplateOutputMeta> templateOutputMetas = templateOutputMetaDao.findByRuleTemplate(templateInDb);
        List<Integer> types = templateDataSourceTypeDao.findByTemplate(templateInDb).stream().map(TemplateDataSourceType::getDataSourceTypeId).collect(
                Collectors.toList());
        TemplateMetaResponse response = new TemplateMetaResponse(templateInDb, templateOutputMetas, types);

        LOGGER.info("Succeed to get rule_template, rule template id: {}", ruleTemplateId);
        return new GeneralResponse<>("200", "{&GET_RULE_TEMPLATE_META_SUCCESSFULLY}", response);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class}, propagation = Propagation.REQUIRED)
    public RuleTemplateResponse addRuleTemplate(AddRuleTemplateRequest request)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        AddRuleTemplateRequest.checkRequest(request);
        LOGGER.info("Add default rule template request detail: {}", request.toString());

        //集群、数据库、数据表

        // Check user info.
        User userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        Integer roleType = roleService.getRoleType(userRoles);

        List<DepartmentSubInfoRequest> dataVisibilityList = request.getVisibilityDepartmentList();
        boolean allVisibility = false;
        if (CollectionUtils.isNotEmpty(dataVisibilityList)) {
            allVisibility = dataVisibilityList.stream().map(DepartmentSubInfoRequest::getId).anyMatch(ALL_DEPARTMENT_VISIBILITY::equals);
        }
        subDepartmentPermissionService.checkEditablePermission(roleType, userInDb, null, request.getDevDepartmentId(), request.getOpsDepartmentId(), allVisibility);

        // Save template.
        String nowDate = RuleTemplateServiceImpl.PRINT_TIME_FORMAT.format(new Date());
        checkTemplateName(request.getTemplateName());
        if (StringUtils.isNotBlank(request.getEnName())) {
            checkTemplateEnName(request.getEnName());
        }

        Template newTemplate = new Template();
        newTemplate.setName(request.getTemplateName());
        newTemplate.setClusterNum(-1);
        newTemplate.setDbNum(-1);
        newTemplate.setTableNum(-1);
        newTemplate.setFieldNum(-1);
        newTemplate.setActionType(request.getActionType());
        newTemplate.setMidTableAction(request.getMidTableAction());

        if (StringUtils.isNotBlank(request.getCountFunctionName())) {
            String value = "";

            if (FunctionTypeEnum.COUNT_FUNCTION.getFunction().equals(request.getCountFunctionName())) {
                value = "*";
            } else if (StringUtils.isNotBlank(request.getCountFunctioAlias())) {
                value = request.getCountFunctioAlias();
            }
            newTemplate.setShowSql(request.getMidTableAction().replaceFirst(QualitisConstants.ASTERISK, request.getCountFunctionName() + "(" + value + ")"));
        }

        newTemplate.setTemplateType(request.getTemplateType());
        newTemplate.setImportExportName(UuidGenerator.generate());
        newTemplate.setDevDepartmentName(request.getDevDepartmentName());
        newTemplate.setOpsDepartmentName(request.getOpsDepartmentName());
        newTemplate.setDevDepartmentId(request.getDevDepartmentId());
        newTemplate.setOpsDepartmentId(request.getOpsDepartmentId());
        newTemplate.setCreateUser(userInDb);
        newTemplate.setCreateTime(nowDate);

        newTemplate.setEnName(request.getEnName());
        newTemplate.setDescription(request.getDescription());
        newTemplate.setVerificationLevel(request.getVerificationLevel());
        newTemplate.setVerificationType(request.getVerificationType());
        newTemplate.setSaveMidTable(request.getSaveMidTable() != null ? request.getSaveMidTable() : false);
        newTemplate.setFilterFields(request.getFilterFields() != null ? request.getFilterFields() : false);
        newTemplate.setWhetherUsingFunctions(request.getWhetherUsingFunctions() != null ? request.getWhetherUsingFunctions() : false);
        newTemplate.setVerificationCnName(request.getVerificationCnName());
        newTemplate.setVerificationEnName(request.getVerificationEnName());
        newTemplate.setNamingMethod(request.getNamingMethod());
        newTemplate.setWhetherSolidification(request.getWhetherSolidification() != null ? request.getWhetherSolidification() : false);
        newTemplate.setCheckTemplate(request.getCheckTemplate());
        newTemplate.setMajorType(request.getMajorType());
        newTemplate.setTemplateNumber(request.getTemplateNumber());
        newTemplate.setCustomZhCode(request.getCustomZhCode());

        Template savedTemplate = ruleTemplateDao.saveTemplate(newTemplate);

        batchHandleFunctionAndFields(savedTemplate, request.getUdfFunctionName());

        for (Integer type : request.getDatasourceType()) {
            TemplateDataSourceType templateDataSourceType = new TemplateDataSourceType(type, savedTemplate);
            templateDataSourceTypeDao.save(templateDataSourceType);
        }
        LOGGER.info("Succeed to save rule template, template_id: {}", savedTemplate.getId());
        // Save template info.
        createAndSaveTemplateInfo(savedTemplate, request);

//        Save data visibility
        RuleTemplateResponse ruleTemplateResponse = new RuleTemplateResponse(savedTemplate, false);
        List<DepartmentSubInfoResponse> departmentInfoResponses = dataVisibilityService.saveBatch(savedTemplate.getId(), TableDataTypeEnum.RULE_TEMPLATE, request.getVisibilityDepartmentList());
        ruleTemplateResponse.setVisibilityDepartmentNameList(departmentInfoResponses);
        return ruleTemplateResponse;
    }

    private void createAndSaveTemplateInfo(Template savedTemplate, AddRuleTemplateRequest request) {
        // Save template output meta.
        if (StringUtils.isNotBlank(request.getCountFunctionName())) {
            Set<TemplateOutputMeta> templateOutputMetas = new HashSet<>();

            templateOutputMetas.addAll(templateOutputMetaService.getAndSaveTemplateOutputMeta(QualitisConstants.DISSATISFACTION + request.getTemplateName() + QualitisConstants.NUMS,
                    FunctionTypeEnum.getFunctionTypeByName(request.getCountFunctionName()),
                    request.getSaveMidTable(), savedTemplate, request.getSamplingContent()));
            savedTemplate.setTemplateOutputMetas(templateOutputMetas);
            LOGGER.info("Success to save template output meta. TemplateOutputMetas: {}", savedTemplate.getTemplateOutputMetas());
        } else if (RuleTemplateTypeEnum.FILE_COUSTOM.getCode().equals(savedTemplate.getTemplateType()) && StringUtils.isNotBlank(request.getSamplingContent())) {
            Set<TemplateOutputMeta> templateOutputMetas = new HashSet<>();

            templateOutputMetas.addAll(templateOutputMetaService.getAndSaveTemplateOutputMeta(null, null,
                    request.getSaveMidTable(), savedTemplate, request.getSamplingContent()));
            savedTemplate.setTemplateOutputMetas(templateOutputMetas);
            LOGGER.info("Success to save template output meta. TemplateOutputMetas: {}", savedTemplate.getTemplateOutputMetas());
        }

        // Save template mid_table input meta
        List<TemplateMidTableInputMeta> templateMidTableInputMetas = new ArrayList<>();
        for (TemplateMidTableInputMetaRequest templateMidTableInputMetaRequest : request.getTemplateMidTableInputMetaRequests()) {

            if (templateMidTableInputMetaRequest.getInputType().equals(TemplateInputTypeEnum.VALUE_RANGE.getCode()) && templateMidTableInputMetaRequest.getWhetherNewValue()) {
                templateMidTableInputMetas.addAll(handleValueRange(savedTemplate));
                continue;
            }
            TemplateMidTableInputMeta templateMidTableInputMeta = new TemplateMidTableInputMeta();
            templateMidTableInputMeta.setName(templateMidTableInputMetaRequest.getName());
            templateMidTableInputMeta.setCnName(templateMidTableInputMetaRequest.getCnName());
            templateMidTableInputMeta.setEnName(templateMidTableInputMetaRequest.getEnName());
            templateMidTableInputMeta.setCnDescription(templateMidTableInputMetaRequest.getCnDescription());
            templateMidTableInputMeta.setEnDescription(templateMidTableInputMetaRequest.getEnDescription());
            templateMidTableInputMeta.setFieldMultipleChoice(templateMidTableInputMetaRequest.getFieldMultipleChoice() != null ? templateMidTableInputMetaRequest.getFieldMultipleChoice() : false);
            templateMidTableInputMeta.setWhetherNewValue(templateMidTableInputMetaRequest.getWhetherNewValue() != null ? templateMidTableInputMetaRequest.getWhetherNewValue() : false);
            templateMidTableInputMeta.setFieldType(templateMidTableInputMetaRequest.getFieldType());
            templateMidTableInputMeta.setInputType(templateMidTableInputMetaRequest.getInputType());
            templateMidTableInputMeta.setPlaceholder(templateMidTableInputMetaRequest.getPlaceholder().replaceAll("\\p{Punct}", ""));
            templateMidTableInputMeta.setPlaceholderDescription(templateMidTableInputMetaRequest.getPlaceholderDescription());
            templateMidTableInputMeta.setRegexpType(templateMidTableInputMetaRequest.getRegexpType());
            templateMidTableInputMeta.setReplaceByRequest(templateMidTableInputMetaRequest.getReplaceByRequest());
            templateMidTableInputMeta.setTemplate(savedTemplate);
            templateMidTableInputMetas.add(templateMidTableInputMeta);
        }
        savedTemplate.setTemplateMidTableInputMetas(templateMidTableInputMetaService.saveAll(templateMidTableInputMetas));
        LOGGER.info("Success to save template mid_table input meta. TemplateMidTableInputMetas: {}", savedTemplate.getTemplateMidTableInputMetas());


        // Save template statistics input meta
        if (StringUtils.isNotBlank(request.getCountFunctionName())) {
            List<TemplateStatisticsInputMeta> templateStatisticsInputMetas = new ArrayList<>();

            TemplateStatisticsInputMeta templateStatisticsInputMeta = new TemplateStatisticsInputMeta();
            templateStatisticsInputMeta.setName(request.getTemplateName());
            templateStatisticsInputMeta.setPureName(request.getTemplateName());
            templateStatisticsInputMeta.setFuncName(request.getCountFunctionName());
            templateStatisticsInputMeta.setResultType(QualitisConstants.DATA_TYPE_LONG);
            if (FunctionTypeEnum.COUNT_FUNCTION.getFunction().equals(request.getCountFunctionName())) {
                templateStatisticsInputMeta.setValue("*");
            } else if (StringUtils.isNotBlank(request.getCountFunctioAlias())) {
                templateStatisticsInputMeta.setValue(request.getCountFunctioAlias());
            }
            templateStatisticsInputMeta.setValueType(FieldTypeEnum.NUMBER.getCode());
            templateStatisticsInputMeta.setTemplate(savedTemplate);
            templateStatisticsInputMetas.add(templateStatisticsInputMeta);

            savedTemplate.setStatisticAction(templateStatisticsInputMetaService.saveAll(templateStatisticsInputMetas));
            LOGGER.info("Success to save template statistics input meta. templateStatisticsInputMetas: {}", savedTemplate.getStatisticAction());
        }

    }

    /**
     * 数值范围 + 发现新值，要存数值范围，不带输入框之外、另外存最小值、表达式、最大值共三个
     *
     * @param template
     * @return
     */
    private List<TemplateMidTableInputMeta> handleValueRange(Template template) {
        List<TemplateMidTableInputMeta> templateMidTableInputMetas = new ArrayList<>();
        TemplateMidTableInputMeta templateMidTableInputMeta = new TemplateMidTableInputMeta();
        templateMidTableInputMeta.setName(QualitisConstants.INTERMEDIATE_EXPRESSION);
        templateMidTableInputMeta.setInputType(TemplateInputTypeEnum.INTERMEDIATE_EXPRESSION.getCode());
        templateMidTableInputMeta.setPlaceholder(QualitisConstants.INTERMEDIATE_PLACEHOLDER);
        templateMidTableInputMeta.setPlaceholderDescription(QualitisConstants.INTERMEDIATE_PLACEHOLDER_DESCRIPTION);
        templateMidTableInputMeta.setReplaceByRequest(false);
        templateMidTableInputMeta.setWhetherNewValue(false);
        templateMidTableInputMeta.setFieldMultipleChoice(false);
        templateMidTableInputMeta.setTemplate(template);
        templateMidTableInputMetas.add(templateMidTableInputMeta);

        TemplateMidTableInputMeta maxTemplateMidTableInputMeta = new TemplateMidTableInputMeta();
        maxTemplateMidTableInputMeta.setName(QualitisConstants.MAXIMUM);
        maxTemplateMidTableInputMeta.setInputType(TemplateInputTypeEnum.MAXIMUM.getCode());
        maxTemplateMidTableInputMeta.setPlaceholder(QualitisConstants.MAXIMUM_PLACEHOLDER);
        maxTemplateMidTableInputMeta.setPlaceholderDescription(QualitisConstants.MAXIMUM_PLACEHOLDER_DESCRIPTION);
        maxTemplateMidTableInputMeta.setReplaceByRequest(false);
        maxTemplateMidTableInputMeta.setWhetherNewValue(false);
        maxTemplateMidTableInputMeta.setFieldMultipleChoice(false);
        maxTemplateMidTableInputMeta.setTemplate(template);
        templateMidTableInputMetas.add(maxTemplateMidTableInputMeta);

        TemplateMidTableInputMeta minTemplateMidTableInputMeta = new TemplateMidTableInputMeta();
        minTemplateMidTableInputMeta.setName(QualitisConstants.MINIMUM);
        minTemplateMidTableInputMeta.setInputType(TemplateInputTypeEnum.MINIMUM.getCode());
        minTemplateMidTableInputMeta.setPlaceholder(QualitisConstants.MINIMUM_PLACEHOLDER);
        minTemplateMidTableInputMeta.setPlaceholderDescription(QualitisConstants.MINIMUM_PLACEHOLDER_DESCRIPTION);
        minTemplateMidTableInputMeta.setReplaceByRequest(false);
        minTemplateMidTableInputMeta.setWhetherNewValue(false);
        minTemplateMidTableInputMeta.setFieldMultipleChoice(false);
        minTemplateMidTableInputMeta.setTemplate(template);
        templateMidTableInputMetas.add(minTemplateMidTableInputMeta);

        return templateMidTableInputMetas;
    }


    private void checkTemplateName(String templateName) throws UnExpectedRequestException {
        boolean isExisted = ruleTemplateDao.getDefaultByName(templateName).isPresent();
        if (isExisted) {
            throw new UnExpectedRequestException("Template name {&ALREADY_EXIST}");
        }
    }

    private void checkTemplateEnName(String enName) throws UnExpectedRequestException {
        List<Template> templateList = ruleTemplateDao.findTemplateByEnName(enName);
        if (CollectionUtils.isNotEmpty(templateList)) {
            throw new UnExpectedRequestException("Template En name {&ALREADY_EXIST}");
        }
    }


    /**
     * 校验规则是否存在有配置了该模板name
     *
     * @param templateInDb
     * @throws UnExpectedRequestException
     */
    public void checkExistRuleByTemplate(Template templateInDb) throws UnExpectedRequestException {
        List<Rule> ruleList = ruleDao.findByTemplate(templateInDb);
        if (CollectionUtils.isNotEmpty(ruleList)) {
            LOGGER.info("This is ruleList", Arrays.toString(ruleList.toArray()));
            throw new UnExpectedRequestException("{&TEMPLATE_ALREADY_CONFIGURED_IN_THE_RULE}.");
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public RuleTemplateResponse modifyRuleTemplate(ModifyRuleTemplateRequest request)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        AddRuleTemplateRequest addRuleTemplateRequest = ModifyRuleTemplateRequest.checkRequest(request);
        // Check template existence
        Template templateInDb = checkRuleTemplate(request.getTemplateId());

        //checkExistRuleByTemplate(templateInDb);

        // Check user info.
        User userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        Integer roleType = roleService.getRoleType(userRoles);

        List<DepartmentSubInfoRequest> dataVisibilityList = request.getVisibilityDepartmentNameList();
        boolean allVisibility = false;
        if (CollectionUtils.isNotEmpty(dataVisibilityList)) {
            allVisibility = dataVisibilityList.stream().map(DepartmentSubInfoRequest::getId).anyMatch(ALL_DEPARTMENT_VISIBILITY::equals);
        }

        String createUser = Objects.nonNull(templateInDb.getCreateUser()) ? templateInDb.getCreateUser().getUsername() : null;
        subDepartmentPermissionService.checkEditablePermission(roleType, userInDb, createUser, request.getDevDepartmentId(), request.getOpsDepartmentId(), allVisibility);

        // delete output meta
        templateOutputMetaService.deleteByTemplate(templateInDb);
        // delete mid_table input meta
        templateMidTableInputMetaService.deleteByTemplate(templateInDb);
        // delete statistics input meta
        templateStatisticsInputMetaService.deleteByTemplate(templateInDb);
        // delete template type relationship
        templateDataSourceTypeDao.deleteByTemplate(templateInDb);
        // delete Udf Function
        templateUdfDao.deleteByTemplate(templateInDb);

        // Save template.
        if (!templateInDb.getName().equals(request.getTemplateName())) {
            checkTemplateName(request.getTemplateName());
        }
        //enName
        if (!templateInDb.getEnName().equals(request.getEnName()) && StringUtils.isNotBlank(request.getEnName())) {
            checkTemplateEnName(request.getEnName());
        }

        String nowDate = RuleTemplateServiceImpl.PRINT_TIME_FORMAT.format(new Date());
        templateInDb.setName(request.getTemplateName());
        templateInDb.setActionType(request.getActionType());

        templateInDb.setMidTableAction(request.getMidTableAction());
        templateInDb.setSaveMidTable(request.getSaveMidTable());
        if (StringUtils.isNotBlank(request.getCountFunctionName())) {
            String value = "";

            if (FunctionTypeEnum.COUNT_FUNCTION.getFunction().equals(request.getCountFunctionName())) {
                value = "*";
            } else if (StringUtils.isNotBlank(request.getCountFunctioAlias())) {
                value = request.getCountFunctioAlias();
            }
            templateInDb.setShowSql(request.getMidTableAction().replaceFirst(QualitisConstants.ASTERISK, request.getCountFunctionName() + "(" + value + ")"));
        }
        templateInDb.setTemplateType(request.getTemplateType());
        templateInDb.setModifyUser(userInDb);
        templateInDb.setModifyTime(nowDate);
        templateInDb.setDevDepartmentName(request.getDevDepartmentName());
        templateInDb.setOpsDepartmentName(request.getOpsDepartmentName());
        templateInDb.setDevDepartmentId(request.getDevDepartmentId());
        templateInDb.setOpsDepartmentId(request.getOpsDepartmentId());

        templateInDb.setEnName(request.getEnName());
        templateInDb.setDescription(request.getDescription());
        templateInDb.setVerificationLevel(request.getVerificationLevel());
        templateInDb.setVerificationType(request.getVerificationType());
        templateInDb.setSaveMidTable(request.getSaveMidTable() != null ? request.getSaveMidTable() : false);
        templateInDb.setFilterFields(request.getFilterFields() != null ? request.getFilterFields() : false);
        templateInDb.setWhetherUsingFunctions(request.getWhetherUsingFunctions() != null ? request.getWhetherUsingFunctions() : false);
        templateInDb.setVerificationCnName(request.getVerificationCnName());
        templateInDb.setVerificationEnName(request.getVerificationEnName());
        templateInDb.setNamingMethod(request.getNamingMethod());
        templateInDb.setWhetherSolidification(request.getWhetherSolidification() != null ? request.getWhetherSolidification() : false);
        templateInDb.setCheckTemplate(request.getCheckTemplate());
        templateInDb.setMajorType(request.getMajorType());
        templateInDb.setTemplateNumber(request.getTemplateNumber());
        templateInDb.setCustomZhCode(request.getCustomZhCode());

        Template savedTemplate = ruleTemplateDao.saveTemplate(templateInDb);
        List<TemplateDataSourceType> templateDataSourceTypes = templateDataSourceTypeDao.findByTemplate(savedTemplate);
        List<Integer> templateDataSourceTypeIntegers = templateDataSourceTypes.stream().map(TemplateDataSourceType::getDataSourceTypeId).collect(Collectors.toList());
        for (Integer type : request.getDatasourceType()) {
            if (!templateDataSourceTypeIntegers.contains(type)) {
                TemplateDataSourceType templateDataSourceType = new TemplateDataSourceType(type, savedTemplate);
                templateDataSourceTypeDao.save(templateDataSourceType);
            }

        }

        batchHandleFunctionAndFields(savedTemplate, request.getUdfFunctionName());

        LOGGER.info("Succeed to save rule template, template_id: {}", savedTemplate.getId());
        // Save template info.
        createAndSaveTemplateInfo(savedTemplate, addRuleTemplateRequest);
        RuleTemplateResponse ruleTemplateResponse = new RuleTemplateResponse(savedTemplate, false);
        dataVisibilityService.delete(savedTemplate.getId(), TableDataTypeEnum.RULE_TEMPLATE);
        List<DepartmentSubInfoResponse> departmentInfoResponses = dataVisibilityService.saveBatch(savedTemplate.getId(), TableDataTypeEnum.RULE_TEMPLATE, request.getVisibilityDepartmentNameList());
        ruleTemplateResponse.setVisibilityDepartmentNameList(departmentInfoResponses);
        return ruleTemplateResponse;
    }

    private void batchHandleFunctionAndFields(Template savedTemplate, List<String> udfFunctionName) {
        if (savedTemplate.getWhetherUsingFunctions() && CollectionUtils.isNotEmpty(udfFunctionName)) {
            List<TemplateUdf> collect = udfFunctionName.stream().map(temp -> {
                TemplateUdf templateUdf = new TemplateUdf();
                templateUdf.setName(temp);
                templateUdf.setTemplate(savedTemplate);
                return templateUdf;
            }).collect(Collectors.toList());
            templateUdfDao.saveAll(collect);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteRuleTemplate(Long templateId) throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check template existence
        Template templateInDb = checkRuleTemplate(templateId);

        checkExistRuleByTemplate(templateInDb);
        // Check operator permission
        User userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        Integer roleType = roleService.getRoleType(userRoles);

        List<DataVisibility> dataVisibilityList = dataVisibilityService.filter(templateId, TableDataTypeEnum.RULE_TEMPLATE);
        boolean allVisibility = false;
        if (CollectionUtils.isNotEmpty(dataVisibilityList)) {
            allVisibility = dataVisibilityList.stream().map(DataVisibility::getDepartmentSubId).anyMatch(ALL_DEPARTMENT_VISIBILITY::equals);
        }
        String createUser = Objects.nonNull(templateInDb.getCreateUser()) ? templateInDb.getCreateUser().getUsername() : null;
        subDepartmentPermissionService.checkEditablePermission(roleType, userInDb, createUser, templateInDb.getDevDepartmentId(), templateInDb.getOpsDepartmentId(), allVisibility);

        // Check rules of template
        ruleService.checkRuleOfTemplate(templateInDb);
        // Delete 'Templatedepartment' or 'TemplateUser'
        clearTemplateUser(templateInDb);

        List<TemplateDataSourceType> templateDataSourceTypes = templateDataSourceTypeDao.findByTemplate(templateInDb);
        for (TemplateDataSourceType templateDataSourceType : templateDataSourceTypes) {
            templateDataSourceTypeDao.delete(templateDataSourceType);
        }

        ruleTemplateDao.deleteTemplate(templateInDb);

        dataVisibilityService.delete(templateInDb.getId(), TableDataTypeEnum.RULE_TEMPLATE);
    }

    @Override
    public void deleteFileRuleTemplate(Long templateId) throws UnExpectedRequestException {
        // Check template existence
        Template templateInDb = checkRuleTemplate(templateId);
        ruleTemplateDao.deleteTemplate(templateInDb);
        List<TemplateDataSourceType> templateDataSourceTypes = templateDataSourceTypeDao.findByTemplate(templateInDb);
        for (TemplateDataSourceType templateDataSourceType : templateDataSourceTypes) {
            templateDataSourceTypeDao.delete(templateDataSourceType);
        }
    }

    private void clearTemplateUser(Template templateInDb) {
        TemplateUser templateUser = templateUserDao.findByTemplate(templateInDb);
        if (templateUser != null) {
            templateUserDao.delete(templateUser);
        }
    }

    @Override
    public RuleTemplateResponse getModifyRuleTemplateDetail(Long templateId) throws UnExpectedRequestException {
        // Check template existence
        Template templateInDb = checkRuleTemplate(templateId);
        DataVisibilityPermissionDto dataVisibilityPermissionDto = new DataVisibilityPermissionDto.Builder()
                .createUser(Objects.isNull(templateInDb.getCreateUser()) ? null : templateInDb.getCreateUser().getUsername())
                .devDepartmentId(templateInDb.getDevDepartmentId())
                .opsDepartmentId(templateInDb.getOpsDepartmentId())
                .build();
        subDepartmentPermissionService.checkAccessiblePermission(templateId, TableDataTypeEnum.RULE_TEMPLATE, dataVisibilityPermissionDto);

        RuleTemplateResponse response = new RuleTemplateResponse(templateInDb, true);
        response.setClusterNum(templateInDb.getClusterNum());
        response.setDbNum(templateInDb.getDbNum());
        response.setTableNum(templateInDb.getTableNum());
        response.setFieldNum(templateInDb.getFieldNum());
        List<TemplateDataSourceType> templateDataSourceTypes = templateDataSourceTypeDao.findByTemplate(templateInDb);
        response.setDatasourceType(templateDataSourceTypes.stream().map(TemplateDataSourceType::getDataSourceTypeId).collect(Collectors.toList()));

        response.setActionType(templateInDb.getActionType());
        response.setMidTableAction(templateInDb.getMidTableAction());
        response.setSaveMidTable(templateInDb.getSaveMidTable());
        List<TemplateOutputMetaResponse> outputMetaResponses = new ArrayList<>(1);
        List<TemplateMidTableInputMetaResponse> midTableInputMetaResponses = new ArrayList<>(2);
        List<TemplateStatisticsInputMetaResponse> statisticsInputMetaResponses = new ArrayList<>(1);
        for (TemplateOutputMeta templateOutputMeta : templateInDb.getTemplateOutputMetas()) {
            TemplateOutputMetaResponse templateOutputMetaResponse = new TemplateOutputMetaResponse();
            templateOutputMetaResponse.setOutputName(templateOutputMeta.getOutputName());
            outputMetaResponses.add(templateOutputMetaResponse);
        }
        response.setTemplateOutputMetaResponses(outputMetaResponses);

        for (TemplateMidTableInputMeta templateMidTableInputMeta : templateInDb.getTemplateMidTableInputMetas()) {
            boolean present = QualitisConstants.ELIMINATE_PLACEHOLDER.stream().filter(item -> item.equals(templateMidTableInputMeta.getInputType())).findAny().isPresent();
            if (Boolean.TRUE.equals(present)) {
                continue;
            }
            TemplateMidTableInputMetaResponse templateMidTableInputMetaResponse = new TemplateMidTableInputMetaResponse();
            templateMidTableInputMetaResponse.setMidTableInputId(templateMidTableInputMeta.getId());
            templateMidTableInputMetaResponse.setName(templateMidTableInputMeta.getName());
            templateMidTableInputMetaResponse.setPlaceholder(templateMidTableInputMeta.getPlaceholder());
            templateMidTableInputMetaResponse.setPlaceholderDescription(templateMidTableInputMeta.getPlaceholderDescription());
            templateMidTableInputMetaResponse.setInputType(templateMidTableInputMeta.getInputType());
            templateMidTableInputMetaResponse.setCnName(templateMidTableInputMeta.getCnName());
            templateMidTableInputMetaResponse.setEnName(templateMidTableInputMeta.getEnName());
            templateMidTableInputMetaResponse.setCnDescription(templateMidTableInputMeta.getCnDescription());
            templateMidTableInputMetaResponse.setEnDescription(templateMidTableInputMeta.getEnDescription());
            templateMidTableInputMetaResponse.setFieldMultipleChoice(templateMidTableInputMeta.getFieldMultipleChoice());
            templateMidTableInputMetaResponse.setWhetherNewValue(templateMidTableInputMeta.getWhetherNewValue());
            midTableInputMetaResponses.add(templateMidTableInputMetaResponse);
        }
        response.setTemplateMidTableInputMetaResponses(midTableInputMetaResponses);
        for (TemplateStatisticsInputMeta templateStatisticsInputMeta : templateInDb.getStatisticAction()) {
            TemplateStatisticsInputMetaResponse templateStatisticsInputMetaResponse = new TemplateStatisticsInputMetaResponse();
            templateStatisticsInputMetaResponse.setName(templateStatisticsInputMeta.getName());
            templateStatisticsInputMetaResponse.setFuncName(templateStatisticsInputMeta.getFuncName());
            templateStatisticsInputMetaResponse.setValue(templateStatisticsInputMeta.getValue());
            templateStatisticsInputMetaResponse.setValueType(templateStatisticsInputMeta.getValueType());
            statisticsInputMetaResponses.add(templateStatisticsInputMetaResponse);
        }
        response.setTemplateStatisticsInputMetaResponses(statisticsInputMetaResponses);

        setBaseInfo(templateId, templateInDb, response, true);
        return response;
    }

    private void setBaseInfo(Long templateId, Template templateInDb, RuleTemplateResponse response, Boolean flag) throws UnExpectedRequestException {
        List<DataVisibility> dataVisibilityList = dataVisibilityService.filter(templateId, TableDataTypeEnum.RULE_TEMPLATE);
        if (CollectionUtils.isNotEmpty(dataVisibilityList)) {
            List<DepartmentSubInfoResponse> departmentInfoResponses = dataVisibilityList.stream().map(DepartmentSubInfoResponse::new).collect(Collectors.toList());
            response.setVisibilityDepartmentNameList(departmentInfoResponses);
        }

        if (flag) {
            User createUser = templateInDb.getCreateUser();
            String createUserName = Objects.nonNull(createUser) ? createUser.getUsername() : Strings.EMPTY;
//             不调用CMDB
            String userName = HttpUtils.getUserName(httpServletRequest);
            User loginUser = userDao.findByUsername(userName);
            List<UserRole> userRoles = userRoleDao.findByUser(loginUser);
            Integer roleType = roleService.getRoleType(userRoles);
            List<Long> departmentIds = userRoles.stream().map(UserRole::getRole)
                    .filter(Objects::nonNull).map(Role::getDepartment)
                    .filter(Objects::nonNull).map(Department::getId)
                    .collect(Collectors.toList());
            if (Objects.nonNull(loginUser.getDepartment())) {
                departmentIds.add(loginUser.getDepartment().getId());
            }
            boolean isEditable = subDepartmentPermissionService.isEditable(roleType, loginUser, createUserName, templateInDb.getDevDepartmentId(), templateInDb.getOpsDepartmentId(), Collections.emptyList());
            response.setEditable(isEditable);
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class}, propagation = Propagation.REQUIRED)
    public Template addFileTemplate(AddFileRuleRequest request) {
        Template newTemplate = new Template();
        newTemplate.setName(request.getProjectId() + "_" + request.getRuleName() + "_template");
        newTemplate.setTemplateType(RuleTemplateTypeEnum.FILE_COUSTOM.getCode());
        Template savedTemplate = ruleTemplateDao.saveTemplate(newTemplate);
        LOGGER.info("Succeed to save file custom template, template_id: {}", savedTemplate.getId());
        return savedTemplate;
    }


    @Override
    public List<Map<String, Object>> getTemplateOptionList(Integer templateType) throws UnExpectedRequestException {
        User userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        Integer roleType = roleService.getRoleType(userRoles);
        RoleSystemTypeEnum roleSystemTypeEnum = RoleSystemTypeEnum.fromCode(roleType);
        switch (roleSystemTypeEnum) {
            case ADMIN:
                return ruleTemplateDao.findAllTemplatesOptionList(templateType);
            case DEPARTMENT_ADMIN:
                List<Long> departmentIds = userRoles.stream().map(UserRole::getRole).filter(Objects::nonNull)
                        .map(Role::getDepartment).filter(Objects::nonNull)
                        .map(Department::getId).collect(Collectors.toList());
                if (Objects.nonNull(userInDb.getDepartment())) {
                    departmentIds.add(userInDb.getDepartment().getId());
                }
                List<Long> devAndOpsInfoWithDeptList = subDepartmentPermissionService.getSubDepartmentIdList(departmentIds);
                return ruleTemplateDao.findTemplatesOptionList(TableDataTypeEnum.RULE_TEMPLATE.getCode(), devAndOpsInfoWithDeptList, userInDb, templateType);
            default:
                return ruleTemplateDao.findTemplatesOptionList(TableDataTypeEnum.RULE_TEMPLATE.getCode(), Arrays.asList(userInDb.getSubDepartmentCode()), userInDb, templateType);
        }
    }

    @Override
    public List<Map<String, Object>> getAllTemplateInRule() {
        return ruleTemplateDao.findTemplatesOptionListInRule();
    }

    @Override
    public List<Map<String, Object>> getTemplateCheckLevelList() {
        return TemplateCheckLevelEnum.getTemplateCheckLevelList();
    }

    @Override
    public List<Map<String, Object>> getTemplateCheckTypeList() {
        return TemplateCheckTypeEnum.getTemplateCheckTypeList();
    }


    private List<Map<String, Object>> handleSpecial(Integer templateType) {
        List<Map<String, Object>> templateDefaultInputMetas = Lists.newArrayList();
        if (RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode().toString().equals(templateType.toString())) {
            templateDefaultInputMetas = ruleTemplateDao.getTemplateDefaultInputMeta(QualitisConstants.SINGLE_TABLE);
        } else if (RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode().toString().equals(templateType.toString())) {
            templateDefaultInputMetas = ruleTemplateDao.getTemplateDefaultInputMeta(QualitisConstants.CROSS_TABLE);
        } else if (RuleTypeEnum.FILE_TEMPLATE_RULE.getCode().toString().equals(templateType.toString())) {
            templateDefaultInputMetas = ruleTemplateDao.getTemplateDefaultInputMeta(QualitisConstants.FILE_TABLE);
        }
        return templateDefaultInputMetas;
    }

    @Override
    public List<Map<String, Object>> getTemplateFileTypeList() {
        return TemplateFileTypeEnum.getTemplateFileTypeList();
    }

    @Override
    public List<Map<String, Object>> getStatisticalFunctionList() {
        return StatisticalFunctionEnum.getStatisticalFunctionList();
    }

    @Override
    public RuleTemplatePlaceholderResponse getPlaceholderData(Integer templateType) {
        List<Map<String, Object>> templateDefaultInputMetas = handleSpecial(templateType);

        List<TemplateDefaultInputMeta> collect = Lists.newArrayList();
        for (Map<String, Object> u : templateDefaultInputMetas) {
            TemplateDefaultInputMeta templateDefaultInputMeta = new TemplateDefaultInputMeta();
            templateDefaultInputMeta.setId(Long.parseLong(u.get("id").toString()));
            templateDefaultInputMeta.setType((Integer) u.get("type"));
            templateDefaultInputMeta.setPlaceholder((String) u.get("placeholder"));
            templateDefaultInputMeta.setPlaceholderDesc((String) u.get("placeholder_desc"));
            templateDefaultInputMeta.setCnName((String) u.get("cn_name"));
            templateDefaultInputMeta.setEnName((String) u.get("en_name"));
            templateDefaultInputMeta.setCnDesc((String) u.get("cn_desc"));
            templateDefaultInputMeta.setEnDesc((String) u.get("en_desc"));
            templateDefaultInputMeta.setSupportFields((Boolean) u.get("support_fields"));
            templateDefaultInputMeta.setSupportStandard((Boolean) u.get("support_standard"));
            templateDefaultInputMeta.setSupportNewValue((Boolean) u.get("support_new_value"));

            collect.add(templateDefaultInputMeta);
        }

        RuleTemplatePlaceholderResponse ruleTemplatePlaceholderResponse = new RuleTemplatePlaceholderResponse(collect);
        return ruleTemplatePlaceholderResponse;
    }

    @Override
    public void checkAccessiblePermission(Template template) throws UnExpectedRequestException {
        User user = template.getCreateUser();
        DataVisibilityPermissionDto dataVisibilityPermissionDto = new DataVisibilityPermissionDto.Builder()
                .createUser(Objects.nonNull(user) ? user.getUsername() : null)
                .devDepartmentId(template.getDevDepartmentId())
                .opsDepartmentId(template.getOpsDepartmentId())
                .build();
        subDepartmentPermissionService.checkAccessiblePermission(template.getId(), TableDataTypeEnum.RULE_TEMPLATE, dataVisibilityPermissionDto);
    }

    @Override
    public List<Map<String, Object>> getNamingMethodList() {
        return NamingMethodEnum.getNamingMethodList();
    }

    @Override
    public List<NamingConventionsResponse> getConventionsNamingQuery() {
        List<NamingConventions> namingConventionsList = namingConventionsDao.findAll();

        List<NamingConventionsResponse> collectList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(namingConventionsList)) {
            for (NamingConventions namingConventions : namingConventionsList) {
                NamingConventionsResponse namingConventionsResponse = new NamingConventionsResponse();
                namingConventionsResponse.setMajorCategories(StringEscapeUtils.unescapeJava(namingConventions.getMajorCategories()));
                namingConventionsResponse.setKind(StringEscapeUtils.unescapeJava(namingConventions.getKind()));
                namingConventionsResponse.setCreateTime(namingConventions.getCreateTime());
                namingConventionsResponse.setCreateUser(namingConventions.getCreateUser());
                namingConventionsResponse.setModifyUser(namingConventions.getModifyUser());
                namingConventionsResponse.setModifyTime(namingConventions.getModifyTime());
                collectList.add(namingConventionsResponse);
            }

        }

        return collectList;
    }

}
