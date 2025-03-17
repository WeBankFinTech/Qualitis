package com.webank.wedatasphere.qualitis.service.impl;

import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.SystemConfigDao;
import com.webank.wedatasphere.qualitis.entity.SystemConfig;
import com.webank.wedatasphere.qualitis.service.MetricExtInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2024-11-21 17:07
 * @description
 */
@Service
public class MetricExtInfoServiceImpl implements MetricExtInfoService {

    @Autowired
    private SystemConfigDao systemConfigDao;

    @Override
    public List<String> allMonitoringCapabilities() {
        SystemConfig systemConfig = systemConfigDao.findByKeyName(QualitisConstants.SYSTEM_CONFIG_MONITORING_CAPABILITY);
        if (systemConfig != null && StringUtils.isNotBlank(systemConfig.getValue())) {
            String[] monitoringCapabilities = StringUtils.split(systemConfig.getValue(), SpecCharEnum.COMMA.getValue());
            return Arrays.asList(monitoringCapabilities);
        }
        return Collections.emptyList();
    }

}
