package com.webank.wedatasphere.qualitis.rule.builder;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.request.AbstractAddRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleRequest;

/**
 * @author allenzhou@webank.com
 * @date 2021/8/17 11:45
 */
public interface AddRequestBuilder {

    /**
     * Set basic info for param 3.
     * @param datasource
     * @param abortOnFailure
     * @param alertInfo
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    AddRequestBuilder basicInfoWithDataSource(String datasource, boolean abortOnFailure, String alertInfo)
        throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Set basic info for param 5.
     * @param cluster
     * @param sql
     * @param alertInfo
     * @param abortOnFailure
     * @param execParams
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    AddRequestBuilder basicInfoWithDataSource(String cluster, String sql, String alertInfo, boolean abortOnFailure, String execParams)
        throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Set basic info for param 7.
     * @param datasource
     * @param deleteFailCheckResult
     * @param uploadRuleMetricValue
     * @param uploadAbnormalValue
     * @param alertInfo
     * @param abortOnFailure
     * @param execParams
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    AddRequestBuilder basicInfoWithDataSource(String datasource, boolean deleteFailCheckResult, boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
        throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Set basic info for param 8.
     * @param datasource
     * @param regxOrRangeOrMapping
     * @param deleteFailCheckResult
     * @param uploadRuleMetricValue
     * @param uploadAbnormalValue
     * @param alertInfo
     * @param abortOnFailure
     * @param execParams
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    AddRequestBuilder basicInfoWithDataSource(String datasource, String regxOrRangeOrMapping, boolean deleteFailCheckResult, boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
        throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Set basic info for param 9.
     * @param datasource
     * @param param1
     * @param param2
     * @param deleteFailCheckResult
     * @param uploadRuleMetricValue
     * @param uploadAbnormalValue
     * @param alertInfo
     * @param abortOnFailure
     * @param execParams
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    AddRequestBuilder basicInfoWithDataSource(String datasource, String param1, String param2, boolean deleteFailCheckResult, boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
        throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Set basic info for param 10.
     * @param cluster
     * @param datasource
     * @param param1
     * @param param2
     * @param deleteFailCheckResult
     * @param uploadRuleMetricValue
     * @param uploadAbnormalValue
     * @param alertInfo
     * @param abortOnFailure
     * @param execParams
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    AddRequestBuilder basicInfoWithDataSource(String cluster, String datasource, String param1, String param2, boolean deleteFailCheckResult, boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
        throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Add rule metric.
     * @param ruleMetricName
     * @return
     * @throws UnExpectedRequestException
     */
    AddRequestBuilder addRuleMetric(String ruleMetricName) throws UnExpectedRequestException;

    /**
     * Add rule metric with sql check.
     * @param ruleMetricName
     * @param deleteFailCheckResult
     * @param uploadRuleMetricValue
     * @param uploadAbnormalValue
     * @return
     * @throws UnExpectedRequestException
     */
    AddRequestBuilder addRuleMetricWithCheck(String ruleMetricName, boolean deleteFailCheckResult, boolean uploadRuleMetricValue, boolean uploadAbnormalValue) throws UnExpectedRequestException;

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fixValueEqual(String value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fixValueNotEqual(String value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fixValueLessThan(String value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fixValueMoreThan(String value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fixValueLessOrEqual(String value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fixValueMoreOrEqual(String value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder mouthFlux(String value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder weekFlux(String value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder dayFlux(String value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder hourOnHourEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder dayOnDayEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder weekOnWeekEqual(String value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder monthOnMonthEqual(String value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder seasonOnSeasonEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder halfYearOnHalfYearEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fullYearOnFullYearEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder yearOnYearEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder hourOnHourNotEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder dayOnDayNotEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder weekOnWeekNotEqual(String value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder monthOnMonthNotEqual(String value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder seasonOnSeasonNotEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder halfYearOnHalfYearNotEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fullYearOnFullYearNotEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder yearOnYearNotEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder hourOnHourLessThan(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder dayOnDayLessThan(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder weekOnWeekLessThan(String value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder monthOnMonthLessThan(String value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder seasonOnSeasonLessThan(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder halfYearOnHalfYearLessThan(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fullYearOnFullYearLessThan(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder yearOnYearLessThan(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder hourOnHourMoreThan(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder dayOnDayMoreThan(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder weekOnWeekMoreThan(String value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder monthOnMonthMoreThan(String value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder seasonOnSeasonMoreThan(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder halfYearOnHalfYearMoreThan(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fullYearOnFullYearMoreThan(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder yearOnYearMoreThan(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder hourOnHourLessOrEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder dayOnDayLessOrEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder weekOnWeekLessOrEqual(String value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder monthOnMonthLessOrEqual(String value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder seasonOnSeasonLessOrEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder halfYearOnHalfYearLessOrEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fullYearOnFullYearLessOrEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder yearOnYearLessOrEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder hourOnHourMoreOrEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder dayOnDayMoreOrEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder weekOnWeekMoreOrEqual(String value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder monthOnMonthMoreOrEqual(String value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder seasonOnSeasonMoreOrEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder halfYearOnHalfYearMoreOrEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fullYearOnFullYearMoreOrEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder yearOnYearMoreOrEqual(String value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fixValueEqual(double value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fixValueNotEqual(double value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fixValueLessThan(double value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fixValueMoreThan(double value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fixValueLessOrEqual(double value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fixValueMoreOrEqual(double value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder mouthFlux(double value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder weekFlux(double value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder dayFlux(double value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder hourOnHourEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder dayOnDayEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder weekOnWeekEqual(double value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder monthOnMonthEqual(double value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder seasonOnSeasonEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder halfYearOnHalfYearEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fullYearOnFullYearEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder yearOnYearEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder hourOnHourNotEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder dayOnDayNotEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder weekOnWeekNotEqual(double value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder monthOnMonthNotEqual(double value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder seasonOnSeasonNotEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder halfYearOnHalfYearNotEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fullYearOnFullYearNotEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder yearOnYearNotEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder hourOnHourLessThan(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder dayOnDayLessThan(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder weekOnWeekLessThan(double value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder monthOnMonthLessThan(double value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder seasonOnSeasonLessThan(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder halfYearOnHalfYearLessThan(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fullYearOnFullYearLessThan(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder yearOnYearLessThan(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder hourOnHourMoreThan(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder dayOnDayMoreThan(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder weekOnWeekMoreThan(double value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder monthOnMonthMoreThan(double value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder seasonOnSeasonMoreThan(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder halfYearOnHalfYearMoreThan(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fullYearOnFullYearMoreThan(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder yearOnYearMoreThan(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder hourOnHourLessOrEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder dayOnDayLessOrEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder weekOnWeekLessOrEqual(double value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder monthOnMonthLessOrEqual(double value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder seasonOnSeasonLessOrEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder halfYearOnHalfYearLessOrEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fullYearOnFullYearLessOrEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder yearOnYearLessOrEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder hourOnHourMoreOrEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder dayOnDayMoreOrEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder weekOnWeekMoreOrEqual(double value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder monthOnMonthMoreOrEqual(double value);

    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder seasonOnSeasonMoreOrEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder halfYearOnHalfYearMoreOrEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder fullYearOnFullYearMoreOrEqual(double value);
    /**
     * Add check method
     * @param value
     * @return
     */
    AddRequestBuilder yearOnYearMoreOrEqual(double value);
    /**
     * Request
     * @return
     */
    AbstractAddRequest returnRequest();

    /**
     * Set project
     * @param project
     */
    void setProject(Project project);

    /**
     * Set template
     * @param template
     */
    void setTemplate(Template template);

    /**
     * Set rule name
     * @param ruleName
     */
    void setRuleName(String ruleName);

    /**
     * Set rule detail
     * @param ruleDetail
     */
    void setRuleDetail(String ruleDetail);

    /**
     * Set user name
     * @param userName
     */
    void setUserName(String userName);
}
