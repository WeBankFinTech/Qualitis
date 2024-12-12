package com.webank.wedatasphere.qualitis.report.dao;

import com.webank.wedatasphere.qualitis.report.entity.SubscribeOperateReport;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface SubscribeOperateReportDao {

    /**
     * find match operate report
     *
     * @param projectId
     * @param executionFrequency
     * @return
     */
    SubscribeOperateReport findMatchOperateReport(Long projectId, Integer executionFrequency);

    /**
     * Save
     *
     * @param subscribeOperateReport
     * @return
     */
    SubscribeOperateReport save(SubscribeOperateReport subscribeOperateReport);

    /**
     * find by id
     *
     * @param subscribeOperateReportId
     * @return
     */
    SubscribeOperateReport findById(Long subscribeOperateReportId);

    /**
     * delete
     *
     * @param subscribeOperateReport
     */
    void delete(SubscribeOperateReport subscribeOperateReport);

    /**
     * subscribe Operate Report Query
     *
     * @param projectName
     * @param receiver
     * @param projectType
     * @param loginUser
     * @param page
     * @param size
     * @return
     */
    Page<SubscribeOperateReport> subscribeOperateReportQuery(String projectName, String receiver, Integer projectType, String loginUser, int page, int size);

    /**
     * select All Mate Frequency
     *
     * @param executionFrequency
     * @return
     */
    List<SubscribeOperateReport> selectAllMateFrequency(Integer executionFrequency);
}
