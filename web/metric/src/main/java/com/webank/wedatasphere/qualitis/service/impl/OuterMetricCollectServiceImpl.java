package com.webank.wedatasphere.qualitis.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.webank.wedatasphere.qualitis.constant.ImsRuleCalcTypeEnum;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.dao.CalcuUnitDao;
import com.webank.wedatasphere.qualitis.dao.ImsMetricCollectDao;
import com.webank.wedatasphere.qualitis.dao.ImsmetricDataDao;
import com.webank.wedatasphere.qualitis.entity.CalcuUnit;
import com.webank.wedatasphere.qualitis.entity.ImsMetricCollect;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.request.AddMetricCollectRequest;
import com.webank.wedatasphere.qualitis.rule.dao.RuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.service.OuterMetricCollectService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author v_minminghe@webank.com
 * @date 2024-04-16 15:13
 * @description
 */
@Service
public class OuterMetricCollectServiceImpl implements OuterMetricCollectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OuterMetricCollectServiceImpl.class);

    @Autowired
    private ImsMetricCollectDao imsMetricCollectDao;
    @Autowired
    private ImsmetricDataDao imsmetricDataDao;
    @Autowired
    private RuleTemplateDao ruleTemplateDao;
    @Autowired
    private CalcuUnitDao calcuUnitDao;
    @Autowired
    private MetaDataClient metaDataClient;

    @Override
    public void addMetricCollectEnumConfigs(AddMetricCollectRequest collectRequest) throws Exception {
        collectRequest.checkRequest();
        CommonChecker.checkString(collectRequest.getEnumCheckString(), "enum_check_string");
        List<ColumnInfoDetail> columnInfoDetailList = metaDataClient.getColumnInfo(collectRequest.getClusterName(), collectRequest.getDatabase(), collectRequest.getTable(), collectRequest.getProxyUser());
        if (CollectionUtils.isEmpty(columnInfoDetailList)) {
            LOGGER.warn("No any fields. cluster: {}, database: {}, table: {}, user: {}", collectRequest.getClusterName(), collectRequest.getDatabase(), collectRequest.getTable(), collectRequest.getProxyUser());
            throw new UnExpectedRequestException("Failed to get column detail.");
        }

        List<ImsMetricCollect> imsMetricCollectListByTable = new ArrayList<>();
        Map<String, ColumnInfoDetail> columnInfoDetailMap = columnInfoDetailList.stream().collect(Collectors.toMap(ColumnInfoDetail::getFieldName, Function.identity(), (oldVal, newOld) -> oldVal));
        Map<String, Long> calcuUnitNameAndTemplateMap = getCalcuUnitNameAndTemplateIdMap();
        Long enumNumTemplate = calcuUnitNameAndTemplateMap.get(ImsRuleCalcTypeEnum.ENUM_NUM.getDescribe());
        Long enumRateTemplate = calcuUnitNameAndTemplateMap.get(ImsRuleCalcTypeEnum.ENUM_RATE.getDescribe());
        Iterable<String> iterator = Splitter.on(SpecCharEnum.COMMA.getValue()).trimResults().split(collectRequest.getEnumCheckString());
        iterator.forEach(enumStr -> {
            String[] enumField = enumStr.replace("enum_check_", "").split(SpecCharEnum.EQUAL.getValue());
            if (enumField.length == 2) {
                String columnName = enumField[0];
                Boolean isEnum = Integer.valueOf(enumField[1]).equals(1);
                if (isEnum && columnInfoDetailMap.containsKey(columnName)) {
                    ImsMetricCollect enumNumMetriCollect = createImsMetricCollect(enumNumTemplate, columnName, columnInfoDetailMap.get(columnName).getDataType(), collectRequest);
                    ImsMetricCollect enumRateMetriCollect = createImsMetricCollect(enumRateTemplate, columnName, columnInfoDetailMap.get(columnName).getDataType(), collectRequest);
                    imsMetricCollectListByTable.add(enumNumMetriCollect);
                    imsMetricCollectListByTable.add(enumRateMetriCollect);
                }
            }
        });

        List<ImsMetricCollect> imsMetricCollectListInDb = imsMetricCollectDao.findByDatasource(collectRequest.getClusterName(), collectRequest.getDatabase(), collectRequest.getTable(), null, StringUtils.isNotBlank(collectRequest.getPartition()) ? collectRequest.getPartition() : null);
        if (CollectionUtils.isNotEmpty(imsMetricCollectListInDb)) {
            LOGGER.info("Found records in database, ready to clear. request: {}", collectRequest.toString());
            List<Long> ids = imsMetricCollectListInDb.stream().filter(imsMetricCollect -> enumNumTemplate.equals(imsMetricCollect.getTemplateId()) || enumRateTemplate.equals(imsMetricCollect.getTemplateId())).map(ImsMetricCollect::getId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(ids)) {
                imsMetricCollectDao.deleteByIds(ids);
            }
        }
        if (CollectionUtils.isNotEmpty(imsMetricCollectListByTable)) {
            imsMetricCollectDao.saveAll(imsMetricCollectListByTable);
        }
    }

    private Map<String, Long> getCalcuUnitNameAndTemplateIdMap() {
        List<CalcuUnit> calcuUnitList = calcuUnitDao.findAll();
        List<Template> templateList = ruleTemplateDao.findMetricCollectTemplates(calcuUnitList.stream().map(CalcuUnit::getId).collect(Collectors.toList()));
        Map<Long, Template> calcuUnitIdAndTemplateMap = templateList.stream().collect(Collectors.toMap(Template::getCalcuUnitId, Function.identity(), (oldVal, newVal) -> oldVal));
        Map<String, Long> calcuUnitNameAndTemplateMap = Maps.newHashMapWithExpectedSize(calcuUnitList.size());
        for (CalcuUnit calcuUnit : calcuUnitList) {
            if (calcuUnitIdAndTemplateMap.containsKey(calcuUnit.getId())) {
                calcuUnitNameAndTemplateMap.put(calcuUnit.getName(), calcuUnitIdAndTemplateMap.get(calcuUnit.getId()).getId());
            }
        }
        return calcuUnitNameAndTemplateMap;
    }


    private ImsMetricCollect createImsMetricCollect(Long templateId, String columnName, String columnType, AddMetricCollectRequest request) {
        ImsMetricCollect imsMetricCollect = new ImsMetricCollect();
        imsMetricCollect.setExecutionParametersName("default");
        imsMetricCollect.setTemplateId(templateId);
        imsMetricCollect.setClusterName(request.getClusterName());
        imsMetricCollect.setDbName(request.getDatabase());
        imsMetricCollect.setTableName(request.getTable());
        imsMetricCollect.setColumnName(columnName);
        imsMetricCollect.setDatasourceType(request.getDatasourceType());
        imsMetricCollect.setFilter(request.getPartition());
        imsMetricCollect.setColumnType(columnType);
        if (StringUtils.isBlank(imsMetricCollect.getFilter())) {
            imsMetricCollect.setFilter("ds='${run_today}'");
        }
        imsMetricCollect.setCollectAge(0);
        imsMetricCollect.setProxyUser(request.getProxyUser());

        return imsMetricCollect;
    }

}
