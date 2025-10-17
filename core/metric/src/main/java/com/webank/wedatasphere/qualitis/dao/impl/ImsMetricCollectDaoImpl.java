package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.ImsMetricCollectDao;
import com.webank.wedatasphere.qualitis.dao.repository.ImsMetricCollectRepository;
import com.webank.wedatasphere.qualitis.dto.ImsMetricCollectDto;
import com.webank.wedatasphere.qualitis.entity.ImsMetricCollect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author v_minminghe
 */
@Repository
public class ImsMetricCollectDaoImpl implements ImsMetricCollectDao {

    @Autowired
    private ImsMetricCollectRepository imsMetricCollectRepository;

    @Override
    public List<ImsMetricCollect> findByExecIp(String execIp) {
        return imsMetricCollectRepository.findByExecIp(execIp);
    }

    @Override
    public List<ImsMetricCollect> findByDatasource(String clusterName, String dbName, String tableName, String columnName, String filter) {
        return imsMetricCollectRepository.findByDatasource(clusterName, dbName, tableName, columnName, filter);
    }

    @Override
    public List<String> findPartitionList(String clusterName, String dbName, String tableName) {
        return imsMetricCollectRepository.findPartitionList(clusterName, dbName, tableName);
    }

    @Override
    public void saveAll(List<ImsMetricCollect> imsMetricCollectList) {
        imsMetricCollectRepository.saveAll(imsMetricCollectList);
    }

    @Override
    public List<ImsMetricCollect> findByIds(List<Long> ids) {
        return imsMetricCollectRepository.findAllById(ids);
    }

    @Override
    public Optional<ImsMetricCollect> findById(Long id) {
        return imsMetricCollectRepository.findById(id);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        imsMetricCollectRepository.deleteAllById(ids);
    }

    @Override
    public Page<ImsMetricCollectDto> queryListWithPage(String templateEnName, String templateCnName, String cluster, String db, String table, String column, String partition, String createUser, String modifyUser, String createStartTime, String createEndTime, String modifyStartTime, String modifyEndTime, List<String> proxyUsers, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Tuple> tuplePage = imsMetricCollectRepository.queryListWithPage(templateEnName, templateCnName, cluster, db, table, partition, createUser, modifyUser, createStartTime, createEndTime, modifyStartTime, modifyEndTime, proxyUsers, column, pageable);
        List<ImsMetricCollectDto> imsMetricCollectDtoList = tuplePage.getContent().stream().map(tuple -> {
            ImsMetricCollectDto imsMetricCollectDto = new ImsMetricCollectDto();
            imsMetricCollectDto.setId(tuple.get("id", BigInteger.class).longValue());
            imsMetricCollectDto.setTemplateId(tuple.get("templateId", BigInteger.class).longValue());
            imsMetricCollectDto.setTemplateCnName(tuple.get("templateCnName", String.class));
            imsMetricCollectDto.setTemplateEnName(tuple.get("templateEnName", String.class));
            imsMetricCollectDto.setClusterName(tuple.get("clusterName", String.class));
            imsMetricCollectDto.setDbName(tuple.get("dbName", String.class));
            imsMetricCollectDto.setTableName(tuple.get("tableName", String.class));
            imsMetricCollectDto.setPartition(tuple.get("partition", String.class));
            imsMetricCollectDto.setColumnName(tuple.get("columnName", String.class));
            imsMetricCollectDto.setExecutionParametersName(tuple.get("executionParametersName", String.class));
            imsMetricCollectDto.setCreateUser(tuple.get("createUser", String.class));
            imsMetricCollectDto.setModifyUser(tuple.get("modifyUser", String.class));
            imsMetricCollectDto.setCreateTime(tuple.get("createTime", String.class));
            imsMetricCollectDto.setModifyTime(tuple.get("modifyTime", String.class));
            imsMetricCollectDto.setExecFreq(tuple.get("execFreq", String.class));
            imsMetricCollectDto.setProxyUser(tuple.get("proxyUser", String.class));
            return imsMetricCollectDto;
        }).collect(Collectors.toList());
        return new PageImpl<>(imsMetricCollectDtoList, pageable, tuplePage.getTotalElements());
    }

    @Override
    public List<String> queryDistinctProxyUser(String db, String table) {
        return imsMetricCollectRepository.findDistinctProxyUser(db, table);
    }
}
