package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.LinkisMetaDataManager;
import com.webank.wedatasphere.qualitis.metadata.client.OperateCiService;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.request.LinkisConnectParamsRequest;
import com.webank.wedatasphere.qualitis.metadata.request.LinkisDataSourceEnvRequest;
import com.webank.wedatasphere.qualitis.project.request.OuterDataSourceEnvRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.dao.LinkisDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.dao.LinkisDataSourceEnvDao;
import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSourceEnv;
import com.webank.wedatasphere.qualitis.rule.request.GetLinkisDataSourceEnvRequest;
import com.webank.wedatasphere.qualitis.rule.service.LinkisDataSourceEnvService;
import com.webank.wedatasphere.qualitis.rule.service.LinkisDataSourceService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author v_minminghe@webank.com
 * @date 2023-12-01 16:44
 * @description
 */
@Service
public class LinkisDataSourceEnvServiceImpl implements LinkisDataSourceEnvService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkisDataSourceEnvServiceImpl.class);

    @Autowired
    private LinkisDataSourceDao linkisDataSourceDao;
    @Autowired
    private LinkisDataSourceEnvDao linkisDataSourceEnvDao;
    @Autowired
    private OperateCiService operateCiService;
    @Autowired
    private LinkisMetaDataManager linkisMetaDataManager;
    @Autowired
    private LinkisConfig linkisConfig;

    @Override
    public void deleteBatch(List<Long> envIds) {
        linkisDataSourceEnvDao.deleteByEnvIds(envIds);
    }

    @Override
    public void modifyBatch(List<LinkisDataSourceEnv> linkisDataSourceEnvList) {
        linkisDataSourceEnvDao.saveAll(linkisDataSourceEnvList);
    }

    @Override
    public void createBatch(Long linkisDataSourceId, List<LinkisDataSourceEnvRequest> dataSourceEnvRequestList) {
        List<LinkisDataSourceEnv> dataSourceEnvList = dataSourceEnvRequestList.stream().map(dataSourceEnvRequest -> {
            LinkisDataSourceEnv linkisDataSourceEnv = new LinkisDataSourceEnv();
            linkisDataSourceEnv.setLinkisDataSourceId(linkisDataSourceId);
            linkisDataSourceEnv.setEnvId(dataSourceEnvRequest.getId());
            linkisDataSourceEnv.setEnvName(dataSourceEnvRequest.getEnvName());
            linkisDataSourceEnv.setDcnNum(dataSourceEnvRequest.getDcnNum());
            linkisDataSourceEnv.setLogicArea(dataSourceEnvRequest.getLogicArea());
            return linkisDataSourceEnv;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        linkisDataSourceEnvDao.saveAll(dataSourceEnvList);
    }

    @Override
    public List<LinkisDataSourceEnv> queryAllEnvs(Long linkisDataSourceId) {
        List<LinkisDataSourceEnv> linkisDataSourceEnvList = linkisDataSourceEnvDao.queryByLinkisDataSourceId(linkisDataSourceId);

//        compatible with old data
        if (CollectionUtils.isEmpty(linkisDataSourceEnvList)) {
            LinkisDataSource linkisDataSource = linkisDataSourceDao.getByLinkisDataSourceId(linkisDataSourceId);
            if (linkisDataSource != null && StringUtils.isNotBlank(linkisDataSource.getEnvs())) {
                String envs = linkisDataSource.getEnvs();
                String[] envIdAndNameArray = org.apache.commons.lang3.StringUtils.split(envs, SpecCharEnum.COMMA.getValue());
                Map<String, Long> envNameAndIdMap = Maps.newHashMapWithExpectedSize(envIdAndNameArray.length);
                for (String envIdAndName : envIdAndNameArray) {
                    String[] env = org.apache.commons.lang3.StringUtils.split(envIdAndName, SpecCharEnum.COLON.getValue());
                    if (env.length < 2) {
                        LOGGER.warn("Error env format! env: {}", envIdAndName);
                        continue;
                    }
                    envNameAndIdMap.put(env[1].replaceFirst(linkisDataSource.getLinkisDataSourceId() + SpecCharEnum.BOTTOM_BAR.getValue(), ""), Long.valueOf(env[0]));
                }
                linkisDataSourceEnvList = envNameAndIdMap.entrySet().stream().map(entry -> {
                    LinkisDataSourceEnv linkisDataSourceEnv = new LinkisDataSourceEnv();
                    linkisDataSourceEnv.setEnvName(entry.getKey());
                    linkisDataSourceEnv.setEnvId(entry.getValue());
                    return linkisDataSourceEnv;
                }).collect(Collectors.toList());
            }
        }
        return linkisDataSourceEnvList;
    }

    @Override
    public List<LinkisDataSourceEnv> queryEnvsInAdvance(GetLinkisDataSourceEnvRequest request) {
        return linkisDataSourceEnvDao.query(request.getLinkisDataSourceId(), request.getEnvIdList(), request.getDcnNums(), request.getLogicAreas());
    }


}
