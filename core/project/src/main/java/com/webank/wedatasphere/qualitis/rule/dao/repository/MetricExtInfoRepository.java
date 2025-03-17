package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.entity.MetricExtInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2024-11-18 11:51
 * @description
 */
public interface MetricExtInfoRepository extends JpaRepository<MetricExtInfo, Long> {

    /**
     * find by metricId and MetricClass
     * @param metricId
     * @param metricClass
     * @return
     */
    MetricExtInfo findByMetricIdAndMetricClass(Long metricId, String metricClass);

    /**
     * find by metricIds and MetricClass
     * @param metricIds
     * @param metricClass
     * @return
     */
    List<MetricExtInfo> findByMetricIdInAndMetricClass(List<Long> metricIds, String metricClass);

    /**
     * delete by metricId and MetricClass
     * @param metricId
     * @param MetricClass
     * @return
     */
    void deleteByMetricIdAndMetricClass(Long metricId, String MetricClass);
}
