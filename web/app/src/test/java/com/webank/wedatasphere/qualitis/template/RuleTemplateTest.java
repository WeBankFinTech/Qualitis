package com.webank.wedatasphere.qualitis.template;

import com.webank.wedatasphere.qualitis.parser.LocaleParser;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.rule.dao.DataVisibilityDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateDataSourceTypeDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateOutputMetaDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateUserDao;
import com.webank.wedatasphere.qualitis.rule.service.TemplateMidTableInputMetaService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateStatisticsInputMetaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author v_gaojiedeng@webank.com
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RuleTemplateTest {

    @Autowired
    RuleTemplateDao ruleTemplateDao;

    @Autowired
    TemplateDataSourceTypeDao templateDataSourceTypeDao;

    @Autowired
    TemplateOutputMetaDao templateOutputMetaDao;

    @Autowired
    TemplateStatisticsInputMetaService templateStatisticsInputMetaService;

    @Autowired
    private TemplateMidTableInputMetaService templateMidTableInputMetaService;

    @Autowired
    DataVisibilityDao dataVisibilityDao;

    @Autowired
    TemplateUserDao templateUserDao;

    @Autowired
    RuleDao ruleDao;

    @Autowired
    ProjectDao projectDao;

    @Autowired
    private LocaleParser localeParser;

    @Test
    public void test()  {
//        UnExpectedRequestException unExpectedRequestException = new UnExpectedRequestException("Rule name {&ALREADY_EXIST}", 400);
//
//        String message = localeParser.replacePlaceHolderByLocale(unExpectedRequestException.getMessage(), "en_US");
//
//        Response build = Response.ok(StringUtils.isNotEmpty(message) ? unExpectedRequestException.getResponse(message) : unExpectedRequestException.getResponse()).status(unExpectedRequestException.getStatus()).build();

        //System.out.println(build);
//        Rule ces_sync_metadate = ruleDao.findByProjectAndRuleName(project, "ces_sync_metadate");
//
//        ruleDao.saveRule(rule);

        //保存是否成功
//        Template newTemplate = new Template();
//
//        newTemplate.setName("testTemplate");
//        newTemplate.setClusterNum(-1);
//        newTemplate.setDbNum(-1);
//        newTemplate.setTableNum(-1);
//        newTemplate.setFieldNum(-1);
//        newTemplate.setActionType(1);
//        newTemplate.setMidTableAction("select * from test");
//        newTemplate.setShowSql("select count(*) from test");
//        newTemplate.setTemplateType(1);
//        newTemplate.setImportExportName(UuidGenerator.generate());
//        newTemplate.setDevDepartmentName("基础科技产品部/大数据平台室");
//        newTemplate.setOpsDepartmentName("基础科技产品部/大数据平台室");
//        newTemplate.setDevDepartmentId(410140L);
//        newTemplate.setOpsDepartmentId(410140L);
//        newTemplate.setCreateUser(new User());
//        newTemplate.setCreateTime(FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(new Date()));
//        newTemplate.setEnName("test");
//        newTemplate.setDescription("test");
//        newTemplate.setVerificationLevel(1);
//        newTemplate.setVerificationType(1);
//        newTemplate.setSaveMidTable(false);
//        newTemplate.setFilterFields(false);
//        newTemplate.setWhetherUsingFunctions(false);
//        newTemplate.setVerificationCnName("test");
//        newTemplate.setVerificationEnName("test");
//        newTemplate.setNamingMethod(1);
//        newTemplate.setWhetherSolidification(false);
//        newTemplate.setCheckTemplate(null);
//        newTemplate.setMajorType("general_knowledge,Anti");
//        newTemplate.setTemplateNumber("20230323_001");
//        newTemplate.setCustomZhCode("測試");
//
//        Template template = ruleTemplateDao.saveTemplate(newTemplate);
//        //校验对象是否存在
//        assertTrue(template.getId() != 0);
//
//        TemplateDataSourceType templateDataSourceType = new TemplateDataSourceType();
//        templateDataSourceType.setTemplate(template);
//        templateDataSourceType.setDataSourceTypeId(1);
//        TemplateDataSourceType dataSourceType = templateDataSourceTypeDao.save(templateDataSourceType);
//        //校验对象是否存在
//        assertTrue(dataSourceType.getId() != 0);
//
//        TemplateOutputMeta templateOutputMeta = new TemplateOutputMeta();
//        templateOutputMeta.setFieldName("count");
//        templateOutputMeta.setOutputName("不满足test的数量");
//        templateOutputMeta.setOutputEnName(null);
//        templateOutputMeta.setFieldType(1);
//        templateOutputMeta.setTemplate(template);
//        TemplateOutputMeta outputMeta = templateOutputMetaDao.saveTemplateOutputMeta(templateOutputMeta);
//        //校验对象是否存在
//        assertTrue(outputMeta.getId() != 0);
//
//        TemplateMidTableInputMeta templateMidTableInputMeta = new TemplateMidTableInputMeta();
//        templateMidTableInputMeta.setName("数据库");
//        templateMidTableInputMeta.setCnName("数据库");
//        templateMidTableInputMeta.setEnName("database");
//        templateMidTableInputMeta.setCnDescription("数据库元信息");
//        templateMidTableInputMeta.setEnDescription("Database meta info");
//        templateMidTableInputMeta.setFieldMultipleChoice(false);
//        templateMidTableInputMeta.setWhetherStandardValue(false);
//        templateMidTableInputMeta.setWhetherNewValue(false);
//        templateMidTableInputMeta.setFieldType(null);
//        templateMidTableInputMeta.setInputType(5);
//        templateMidTableInputMeta.setPlaceholder("database");
//        templateMidTableInputMeta.setPlaceholderDescription(null);
//        templateMidTableInputMeta.setRegexpType(null);
//        templateMidTableInputMeta.setReplaceByRequest(null);
//        templateMidTableInputMeta.setTemplate(template);
//
//        List<TemplateMidTableInputMeta> list = Lists.newArrayList();
//        list.add(templateMidTableInputMeta);
//        Set<TemplateMidTableInputMeta> templateMidTableInputMetas = templateMidTableInputMetaService.saveAll(list);
//        assertTrue(templateMidTableInputMetas.size() > 0);
//
//        TemplateStatisticsInputMeta templateStatisticsInputMeta = new TemplateStatisticsInputMeta();
//        templateStatisticsInputMeta.setName("測試");
//        templateStatisticsInputMeta.setFuncName("count");
//        templateStatisticsInputMeta.setResultType(QualitisConstants.DATA_TYPE_LONG);
//        templateStatisticsInputMeta.setValue("*");
//        templateStatisticsInputMeta.setValueType(FieldTypeEnum.NUMBER.getCode());
//        templateStatisticsInputMeta.setTemplate(template);
//
//        List<TemplateStatisticsInputMeta> collect = Lists.newArrayList();
//        collect.add(templateStatisticsInputMeta);
//        Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas = templateStatisticsInputMetaService.saveAll(collect);
//        assertTrue(templateStatisticsInputMetas.size() > 0);
//
//        DataVisibility dataVisibility = new DataVisibility();
//        dataVisibility.setTableDataId(1L);
//        dataVisibility.setTableDataType(TableDataTypeEnum.RULE_TEMPLATE.getCode());
//        dataVisibility.setDepartmentSubId(410140L);
//        dataVisibility.setDepartmentSubName("基础科技产品部/大数据平台室");
//
//        List<DataVisibility> dataVisibilityList = Lists.newArrayList();
//        dataVisibilityDao.saveAll(dataVisibilityList);
//
//        //
//        ruleTemplateDao.deleteTemplate(template);
//        Template currentTemplate = ruleTemplateDao.findById(template.getId());
//        assertNull(currentTemplate);
//
//        templateDataSourceTypeDao.delete(dataSourceType);
//
//        dataVisibilityDao.delete(template.getId(), TableDataTypeEnum.RULE_TEMPLATE.getCode());
//        List<DataVisibility> dataVisibilityLists = dataVisibilityDao.findByTableDataIdAndTableDataType(template.getId(), TableDataTypeEnum.RULE_TEMPLATE.getCode());
//        assertTrue(dataVisibilityLists.size() == 0);
    }


}
