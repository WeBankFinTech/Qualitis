package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.rule.dao.LinkisDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSource;
import com.webank.wedatasphere.qualitis.rule.service.LinkisDataSourceService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author v_minminghe@webank.com
 * @date 2023-05-17 14:11
 * @description
 */
@Service
public class LinkisDataSourceServiceImpl implements LinkisDataSourceService {

    private final Logger LOGGER = LoggerFactory.getLogger(LinkisDataSourceServiceImpl.class);

    @Autowired
    private LinkisDataSourceDao linkisDataSourceDao;

    @Override
    public void addOrModify(LinkisDataSource linkisDataSource) throws UnExpectedRequestException {
        validateEnvsParameter(linkisDataSource.getEnvs());
        linkisDataSourceDao.save(linkisDataSource);
    }

    private void validateEnvsParameter(String envs) throws UnExpectedRequestException {
        String[] envArray = StringUtils.split(envs, SpecCharEnum.COMMA.getValue());
        for (String env: envArray) {
            if (StringUtils.split(env, SpecCharEnum.COLON.getValue()).length != 2) {
                throw new UnExpectedRequestException("Illegal env_name!");
            }
        }
    }

    /**
     * 环境ID和环境名称拼接放在LinkisDataSource的envs字段里，并使用冒号进行了分隔
     * @param linkisDataSource
     * @return
     */
    @Override
    public Map<String, Long> getEnvNameAndIdMap(LinkisDataSource linkisDataSource) {
        if (null == linkisDataSource) {
            return Maps.newHashMapWithExpectedSize(0);
        }
        String envs = linkisDataSource.getEnvs();
        if (StringUtils.isBlank(envs)) {
            return Maps.newHashMapWithExpectedSize(0);
        }
        String[] envIdAndNameArray = StringUtils.split(envs, SpecCharEnum.COMMA.getValue());
        Map<String, Long> envNameAndIdMap = Maps.newHashMapWithExpectedSize(envIdAndNameArray.length);
        for (String envIdAndName: envIdAndNameArray) {
            String[] env = StringUtils.split(envIdAndName, SpecCharEnum.COLON.getValue());
            if (env.length < 2) {
                LOGGER.warn("Error env format! env: {}", envIdAndName);
                continue;
            }
            envNameAndIdMap.put(env[1], Long.valueOf(env[0]));
        }
        return envNameAndIdMap;
    }

    @Override
    public List<LinkisDataSource> getByLinkisDataSourceIds(List<Long> linkisDataSourceIds) {
        return linkisDataSourceDao.getByLinkisDataSourceIds(linkisDataSourceIds);
    }

    @Override
    public LinkisDataSource getByLinkisDataSourceName(String linkisDataSourceName) {
        return linkisDataSourceDao.getByLinkisDataSourceName(linkisDataSourceName);
    }

    /**
     * 获取与Linkis侧保持一致的环境名称，该名称经由用户输入的环境名称拼装而成
     * 为了方便区分，用户输入的环境名称称作originalEnvName，而包装后的环境名称则称作linkisEnvName
     * @param linkisDataSource
     * @return
     */
    @Override
    public List<String> getLinkisEnvNameList(LinkisDataSource linkisDataSource) {
        String envs = linkisDataSource.getEnvs();
        if (StringUtils.isBlank(envs)) {
            LOGGER.warn("Request parameters is null: envs");
            return Collections.emptyList();
        }
        String[] envIdAndNameArray = StringUtils.split(envs, SpecCharEnum.COMMA.getValue());
        List<String> linkisEnvNameList = Lists.newArrayListWithExpectedSize(envIdAndNameArray.length);
        for (String envIdAndName: envIdAndNameArray) {
            if (StringUtils.isBlank(envIdAndName)) {
                continue;
            }
            String[] env = StringUtils.split(envIdAndName, SpecCharEnum.COLON.getValue());
            if (env.length < 2) {
                linkisEnvNameList.add(env[0]);
            } else {
                linkisEnvNameList.add(env[1]);
            }
        }
        return linkisEnvNameList;
    }

    /**
     * 获取原始的环境名称，即用户输入的环境名称
     * @param linkisDataSource
     * @return
     */
    @Override
    public List<String> getOriginalEnvNameList(LinkisDataSource linkisDataSource) {
        if (null == linkisDataSource.getLinkisDataSourceId() || null == linkisDataSource.getInputType()) {
            LOGGER.warn("Request parameters is null: linkisDataSourceId or inputType");
            return Collections.emptyList();
        }
        return getLinkisEnvNameList(linkisDataSource).stream()
                .map(linkisEnvName -> convertLinkisEnvNameToOriginal(linkisDataSource.getLinkisDataSourceId(), linkisEnvName, linkisDataSource.getInputType()))
                .collect(Collectors.toList());
    }

    /**
     * 将《环境ID：环境Name》的Map入参，格式化成表字段要求的数据格式，例如：160:192_envName
     * @param envIdAndNameMap
     * @return
     */
    @Override
    public String formatEnvsField(Map<Long, String> envIdAndNameMap) {
        return envIdAndNameMap.entrySet().stream().map(entry -> entry.getKey() + SpecCharEnum.COLON.getValue() + entry.getValue()).collect(Collectors.joining(SpecCharEnum.COMMA.getValue()));
    }

    /**
     * 将前端页面传入的原始环境名称，拼接成linkis侧需要的环境名称。例如原始环境名称是envName，拼接后是：
     * 若是手动录入：168_envName
     * 若是自动录入：164_envName-实例IP
     * 拼接的原因在于：允许用户输入相同的环境名称；区分相同的DCN名称（自动导入）。但Linkis侧却要求环境名称是唯一的
     * @param linkisDataSourceId
     * @param originalEnvName
     * @param inputType
     * @param host
     * @return
     */
    @Override
    public String convertOriginalEnvNameToLinkis(Long linkisDataSourceId, String originalEnvName, Integer inputType, String host) {
        if (isAutoInput(inputType)) {
            return linkisDataSourceId + SpecCharEnum.BOTTOM_BAR.getValue() + originalEnvName + SpecCharEnum.MINUS.getValue() + host;
        }
        return linkisDataSourceId + SpecCharEnum.BOTTOM_BAR.getValue() + originalEnvName;
    }

    /**
     * 与convertOriginalEnvNameToLinkis()方法成对出现，用于反向操作，即将拼接后的环境名称，还原成前端录入的原始环境名称
     * 例如将环境名称164_envName-127.0.0.1，还原成envName
     *
     * @param linkisDataSourceId
     * @param linkisEnvName
     * @param inputType
     * @return
     */
    @Override
    public String convertLinkisEnvNameToOriginal(Long linkisDataSourceId, String linkisEnvName, Integer inputType) {
//        移除数据源ID前缀
        int prefixIndex = 0;
        String prefixStr = linkisDataSourceId + SpecCharEnum.BOTTOM_BAR.getValue();
        if(-1 != linkisEnvName.indexOf(prefixStr)) {
            prefixIndex = prefixStr.length();
        }
//        移除host后缀（如果是自动录入）
        int suffixIndex = linkisEnvName.length();
        if (isAutoInput(inputType)) {
            suffixIndex = linkisEnvName.lastIndexOf(SpecCharEnum.MINUS.getValue());
            suffixIndex = -1 != suffixIndex ? suffixIndex : linkisEnvName.length();
        }
        return StringUtils.substring(linkisEnvName, prefixIndex == -1 ? 0 : prefixIndex, suffixIndex);
    }

    private boolean isAutoInput(Integer inputType) {
        return Integer.valueOf(2).equals(inputType);
    }

    private boolean isSharedVerify(Integer verifyType) {
        return Integer.valueOf(1).equals(verifyType);
    }

}
