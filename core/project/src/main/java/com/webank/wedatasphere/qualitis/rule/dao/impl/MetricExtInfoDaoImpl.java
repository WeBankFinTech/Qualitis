package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.entity.MetricExtInfo;
import com.webank.wedatasphere.qualitis.rule.constant.MetricClassEnum;
import com.webank.wedatasphere.qualitis.rule.dao.MetricExtInfoDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.MetricExtInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author v_minminghe@webank.com
 * @date 2024-11-18 14:15
 * @description
 */
@Repository
public class MetricExtInfoDaoImpl implements MetricExtInfoDao {

    @Autowired
    private MetricExtInfoRepository metricExtInfoRepository;

    @Override
    public MetricExtInfo get(Long metricId, MetricClassEnum metricClass) {
        return metricExtInfoRepository.findByMetricIdAndMetricClass(metricId, metricClass.getCode());
    }

    @Override
    public List<MetricExtInfo> list(List<Long> metricIds, MetricClassEnum metricClass) {
        return metricExtInfoRepository.findByMetricIdInAndMetricClass(metricIds, metricClass.getCode());
    }

    @Override
    public MetricExtInfo save(MetricExtInfo metricExtInfo) {
        return metricExtInfoRepository.save(metricExtInfo);
    }

    @Override
    public void saveAll(List<MetricExtInfo> metricExtInfos) {
        metricExtInfoRepository.saveAll(metricExtInfos);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void delete(Long metricId, MetricClassEnum metricClass) {
        metricExtInfoRepository.deleteByMetricIdAndMetricClass(metricId, metricClass.getCode());
    }

}
