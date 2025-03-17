package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.ImsMetricDao;
//import com.webank.wedatasphere.qualitis.dto.ImsMetricCollectDto;
//import com.webank.wedatasphere.qualitis.entity.ImsMetric;
//import com.webank.wedatasphere.qualitis.util.ScanRulesUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.*;
//import org.springframework.stereotype.Repository;

//import javax.persistence.Tuple;
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.util.Date;
//import java.util.List;
//import java.util.stream.Collectors;

/**
 * @author v_minminghe
 */
//@Repository
public class ImsMetricDaoImpl implements ImsMetricDao {


//    @Override
//    public Page<ImsMetricCollectDto> findImsMetricInAdvance(Long metricId, BigDecimal metricValue, Integer datasourceType, String clusterName, String dbName, String tableName, String columnName, Long templateId, Long dataStartDate, Long dataEndDate, String dataUser, List<String> proxyUsers, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Tuple> tuplePage = imsMetricRepository.findImsMetricInAdvance(datasourceType, clusterName, dbName, tableName, columnName, templateId, dataStartDate, dataEndDate, dataUser, metricId, metricValue, proxyUsers, pageable);
//        List<ImsMetricCollectDto> imsMetricCollectDtoList = tuplePage.getContent().stream().map(tuple -> {
//            ImsMetricCollectDto imsMetricCollectDto = new ImsMetricCollectDto();
//            Tuple metricResult = imsMetricRepository.findImsMetricResultByIdAndDataDate(tuple.get("metric_id", BigInteger.class).longValue(), tuple.get("data_date", BigInteger.class).longValue());
//            imsMetricCollectDto.setMetricId(metricResult.get("metric_id", BigInteger.class).longValue());
//            imsMetricCollectDto.setMetricValue(metricResult.get("metric_value", BigDecimal.class));
//            imsMetricCollectDto.setMetricName(metricResult.get("metric_name", String.class));
//            imsMetricCollectDto.setIdentifyValue(metricResult.get("identify_value", String.class));
//            imsMetricCollectDto.setDatasourceType(metricResult.get("datasource_type", Integer.class));
//            imsMetricCollectDto.setClusterName(metricResult.get("cluster_name", String.class));
//            imsMetricCollectDto.setDbName(metricResult.get("db_name", String.class));
//            imsMetricCollectDto.setTableName(metricResult.get("table_name", String.class));
//            imsMetricCollectDto.setColumnName(metricResult.get("column_name", String.class));
//            imsMetricCollectDto.setCollectId(metricResult.get("metric_collect_id", BigInteger.class).longValue());
//            BigInteger templateIdInDb = metricResult.get("template_id", BigInteger.class);
//            if (templateIdInDb != null) {
//                imsMetricCollectDto.setTemplateId(templateIdInDb.longValue());
//            }
//            imsMetricCollectDto.setDatasourceUser(metricResult.get("datasource_user", String.class));
//            imsMetricCollectDto.setUpdateTime(metricResult.get("update_time", Date.class));
//            BigInteger dataDate = metricResult.get("data_date", BigInteger.class);
//            if (dataDate != null) {
//                imsMetricCollectDto.setDataDate(dataDate.longValue());
//            }
//            return imsMetricCollectDto;
//        }).collect(Collectors.toList());
//
//        return new PageImpl<>(imsMetricCollectDtoList, pageable, tuplePage.getTotalElements());
//    }
//
//    @Override
//    public List<ImsMetricCollectDto> findImsMetric(String clusterName, String dbName, String tableName, String columnName, String calcuUnitName) {
//        List<Tuple> metricCollectList = imsMetricRepository.findImsMetric(clusterName, dbName, tableName, columnName, calcuUnitName);
//        return metricCollectList.stream().map(tuple -> {
//            ImsMetricCollectDto imsMetricCollectDto = new ImsMetricCollectDto();
//            imsMetricCollectDto.setMetricId(tuple.get("metric_id", BigInteger.class).longValue());
//            imsMetricCollectDto.setClusterName(tuple.get("cluster_name", String.class));
//            imsMetricCollectDto.setDbName(tuple.get("db_name", String.class));
//            imsMetricCollectDto.setTableName(tuple.get("table_name", String.class));
//            imsMetricCollectDto.setColumnName(tuple.get("column_name", String.class));
//            imsMetricCollectDto.setCalcuUnitName(tuple.get("calcu_unit_name", String.class));
//            imsMetricCollectDto.setColumnType(tuple.get("column_type", String.class));
//            imsMetricCollectDto.setProxyUser(tuple.get("proxy_user", String.class));
////            imsMetricCollectDto.setIdentifyValue(ScanRulesUtils.auditSenseInfoFromIdentifyValue(tuple.get("identify_value", String.class)));
//            return imsMetricCollectDto;
//        }).collect(Collectors.toList());
//    }
//
//    @Override
//    public List<ImsMetric> findByIds(List<Long> metricIds) {
//        return imsMetricRepository.findAllById(metricIds);
//    }
//
//    @Override
//    public Tuple getMetricIdentifyById(String metricId) {
//        return imsMetricRepository.getMetricIdentifyById(metricId);
//    }
//
//    @Override
//    public List<Tuple> getMetricIdentify(String dbName, String tableName, List<String> calcuUnitNameList) {
//        return imsMetricRepository.getMetricIdentify(dbName, tableName, calcuUnitNameList);
//    }
//
//    @Override
//    public Page<ImsMetric> findWithPage(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
//        return imsMetricRepository.findAll(pageable);
//    }

}
