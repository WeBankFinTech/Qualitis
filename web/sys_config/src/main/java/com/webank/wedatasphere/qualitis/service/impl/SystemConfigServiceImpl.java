package com.webank.wedatasphere.qualitis.service.impl;

import com.webank.wedatasphere.qualitis.config.SystemKeyConfig;
import com.webank.wedatasphere.qualitis.dao.SystemConfigDao;
import com.webank.wedatasphere.qualitis.entity.SystemConfig;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.ModifySystemConfigRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.SystemConfigService;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author howeye
 */
@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    @Autowired
    private SystemKeyConfig systemKeyConfig;

    @Autowired
    private SystemConfigDao systemConfigDao;

    private static Set<String> KEY_NAME_SET;


    @PostConstruct
    public void init() {
        KEY_NAME_SET  = new HashSet<String>() {{
            add(systemKeyConfig.getSaveDatabasePattern());
        }};
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemConfigServiceImpl.class);

    @Override
    @Transactional
    public GeneralResponse<?> modifySystemConfig(ModifySystemConfigRequest request) throws UnExpectedRequestException {
        // Check Argument
        ModifySystemConfigRequest.checkRequest(request);

        checkKeyName(request.getKeyName());

        // Find System Config by key name
        String keyName = request.getKeyName();
        String value = request.getValue();
        SystemConfig systemConfigInDb = systemConfigDao.findByKeyName(keyName);
        if (null == systemConfigInDb) {
            throw new UnExpectedRequestException("key name {&DOES_NOT_EXIST}");
        }
        LOGGER.info("{&SUCCEED_TO_FIND_SYSTEM_CONFIG}. key:{}, value: {}", systemConfigInDb.getKeyName(), systemConfigInDb.getValue());

        // 修改url并保存
        systemConfigInDb.setValue(value);
        SystemConfig savedSystemConfig = systemConfigDao.saveSystemConfig(systemConfigInDb);

        LOGGER.info("{&SUCCEED_TO_MODIFY_SYSTEM_CONFIG}. key: {}, value: {}", savedSystemConfig.getKeyName(), savedSystemConfig.getValue());
        return new GeneralResponse<>("200", "{&SUCCEED_TO_MODIFY_SYSTEM_CONFIG}", null);
    }

    @Override
    public GeneralResponse<?> findByKeyName(String keyName) throws UnExpectedRequestException {
        SystemConfig systemConfigInDb = systemConfigDao.findByKeyName(keyName);

        if (null == systemConfigInDb) {
            throw new UnExpectedRequestException("key name {&DOES_NOT_EXIST}");
        }

        LOGGER.info("{&SUCCEED_TO_FIND_SYSTEM_CONFIG}. key:{}, value: {}", systemConfigInDb.getKeyName(), systemConfigInDb.getValue());
        return new GeneralResponse<>("200", "{&SUCCEED_TO_FIND_SYSTEM_CONFIG}", systemConfigInDb);
    }

    private void checkKeyName(String keyName) throws UnExpectedRequestException {
        if (!KEY_NAME_SET.contains(keyName)) {
            throw new UnExpectedRequestException(String.format("key name: %s is not supported", keyName));
        }
    }
}
