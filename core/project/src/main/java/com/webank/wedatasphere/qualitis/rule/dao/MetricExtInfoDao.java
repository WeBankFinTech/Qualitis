package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.entity.MetricExtInfo;
import com.webank.wedatasphere.qualitis.rule.constant.MetricClassEnum;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2024-11-18 11:53
 * @description
 */
public interface MetricExtInfoDao {

    /**
     * get MetricExtInfo by metricId and MetricClass
     * @param metricId
     * @param metricClass
     * @return
     */
    MetricExtInfo get(Long metricId, MetricClassEnum metricClass);

    /**
     * get MetricExtInfo by metricIds and MetricClass
     * @param metricIds
     * @param metricClass
     * @return
     */
    List<MetricExtInfo> list(List<Long> metricIds, MetricClassEnum metricClass);

    /**
     * save MetricExtInfo
     * @param metricExtInfo
     */
    MetricExtInfo save(MetricExtInfo metricExtInfo);

    /**
     * save MetricExtInfo
     * @param metricExtInfos
     */
    void saveAll(List<MetricExtInfo> metricExtInfos);

    /**
     * delete 
     * @param metricId
     * @param metricClass
     */
    void delete(Long metricId, MetricClassEnum metricClass);

}
