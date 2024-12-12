package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.client.OperateCiService;
import com.webank.wedatasphere.qualitis.metadata.request.LinkisDataSourceRequest;
import com.webank.wedatasphere.qualitis.metadata.response.DepartmentSubResponse;
import com.webank.wedatasphere.qualitis.rule.dao.LinkisDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.dao.LinkisDataSourceEnvDao;
import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSourceEnv;
import com.webank.wedatasphere.qualitis.rule.service.LinkisDataSourceService;
import com.webank.wedatasphere.qualitis.util.DateUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    @Autowired
    private MetaDataClient metaDataClient;
    @Autowired
    private LinkisConfig linkisConfig;
    @Autowired
    private OperateCiService operateCiService;
    @Autowired
    private LinkisDataSourceEnvDao linkisDataSourceEnvDao;

    @Override
    public LinkisDataSource save(Long linkisDataSourceId, Long dataSourceTypeId, LinkisDataSourceRequest request, User userInDb) throws UnExpectedRequestException, JsonProcessingException {
        DepartmentSubResponse cmdbDepartmentResponse = getSubDepartment(userInDb);
        LinkisDataSource linkisDataSource = new LinkisDataSource();
        linkisDataSource.setLinkisDataSourceId(linkisDataSourceId);
        linkisDataSource.setLinkisDataSourceName(request.getDataSourceName());
        linkisDataSource.setDataSourceTypeId(dataSourceTypeId);
        linkisDataSource.setDevDepartmentId(userInDb.getSubDepartmentCode());
        linkisDataSource.setOpsDepartmentId(userInDb.getSubDepartmentCode());
        linkisDataSource.setDevDepartmentName(cmdbDepartmentResponse.getName());
        linkisDataSource.setOpsDepartmentName(cmdbDepartmentResponse.getName());
        linkisDataSource.setCreateUser(userInDb.getUsername());
        linkisDataSource.setCreateTime(DateUtils.now());
        linkisDataSource.setDatasourceDesc(request.getDataSourceDesc());
        linkisDataSource.setLabels(request.getLabels());
        linkisDataSource.setSubSystem(request.getSubSystem());
        linkisDataSource.setInputType(request.getInputType());
        linkisDataSource.setVerifyType(request.getVerifyType());
        linkisDataSource.setDcnRangeType(request.getDcnRangeType());
        linkisDataSource.setVersionId(1L);
        linkisDataSourceDao.save(linkisDataSource);

        return linkisDataSource;
    }

    @Override
    public void modify(LinkisDataSource linkisDataSource, Long dataSourceTypeId, LinkisDataSourceRequest request, User userInDb) throws UnExpectedRequestException, JsonProcessingException {
        DepartmentSubResponse cmdbDepartmentResponse = getSubDepartment(userInDb);
        linkisDataSource.setLinkisDataSourceName(request.getDataSourceName());
        linkisDataSource.setDataSourceTypeId(dataSourceTypeId);
        linkisDataSource.setDevDepartmentId(userInDb.getSubDepartmentCode());
        linkisDataSource.setOpsDepartmentId(userInDb.getSubDepartmentCode());
        linkisDataSource.setDevDepartmentName(cmdbDepartmentResponse.getName());
        linkisDataSource.setOpsDepartmentName(cmdbDepartmentResponse.getName());
        linkisDataSource.setModifyUser(userInDb.getUsername());
        linkisDataSource.setModifyTime(DateUtils.now());

        linkisDataSource.setDatasourceDesc(request.getDataSourceDesc());
        linkisDataSource.setLabels(request.getLabels());
        linkisDataSource.setSubSystem(request.getSubSystem());
        linkisDataSource.setInputType(request.getInputType());
        linkisDataSource.setVerifyType(request.getVerifyType());
        linkisDataSource.setDcnRangeType(request.getDcnRangeType());
        linkisDataSourceDao.save(linkisDataSource);
    }

    @Override
    public void save(LinkisDataSource linkisDataSource) throws UnExpectedRequestException {
        linkisDataSourceDao.save(linkisDataSource);
    }

    private DepartmentSubResponse getSubDepartment(User userInDb) throws UnExpectedRequestException {
        Department department = userInDb.getDepartment();
        if (null == department) {
            throw new UnExpectedRequestException("Invalid department!");
        }
        Long subDepartmentCode = userInDb.getSubDepartmentCode();
        if (null == subDepartmentCode) {
            throw new UnExpectedRequestException("Invalid sub-department");
        }
        List<DepartmentSubResponse> departmentSubResponseList = operateCiService.getDevAndOpsInfo(Integer.valueOf(department.getDepartmentCode()));
        Optional<DepartmentSubResponse> subDeptOptional = departmentSubResponseList.stream()
                .filter(cmdbDepartmentResponse -> subDepartmentCode.equals(Long.valueOf(cmdbDepartmentResponse.getId())))
                .findFirst();
        if (!subDeptOptional.isPresent()) {
            throw new UnExpectedRequestException("Sub department isn't existing");
        }
        return subDeptOptional.get();
    }

    /**
     * Formatting [envs] and convert it to Map
     * Note: The original format of the [envs] is [515:219_env_name1,515:220_env_name2]
     *
     * @param linkisDataSource
     * @return {uat_dev_env: 515}
     */
    @Override
    public Map<String, Long> getEnvNameAndIdMap(LinkisDataSource linkisDataSource) {
        if (null == linkisDataSource) {
            return Collections.emptyMap();
        }
        List<LinkisDataSourceEnv> linkisDataSourceEnvList = linkisDataSourceEnvDao.queryByLinkisDataSourceId(linkisDataSource.getLinkisDataSourceId());
        if (CollectionUtils.isNotEmpty(linkisDataSourceEnvList)) {
            linkisDataSourceEnvList.forEach(linkisDataSourceEnv -> linkisDataSourceEnv.setEnvName(linkisDataSourceEnv.getEnvName().replaceFirst(linkisDataSource.getLinkisDataSourceId() + SpecCharEnum.BOTTOM_BAR.getValue(), "")));
            return linkisDataSourceEnvList.stream().collect(Collectors.toMap(LinkisDataSourceEnv::getEnvName, LinkisDataSourceEnv::getEnvId, (k1, k2) -> k1));
        }
//        You can remove the following code snippet if the data stored in qualitis_linkis_datasource.envs is moved into qualitis_linkis_datasource_env successfully after version 1.4.0
        String envs = linkisDataSource.getEnvs();
        if (StringUtils.isBlank(envs)) {
            return Collections.emptyMap();
        }
        String[] envIdAndNameArray = StringUtils.split(envs, SpecCharEnum.COMMA.getValue());
        Map<String, Long> envNameAndIdMap = Maps.newHashMapWithExpectedSize(envIdAndNameArray.length);
        for (String envIdAndName : envIdAndNameArray) {
            String[] env = StringUtils.split(envIdAndName, SpecCharEnum.COLON.getValue());
            if (env.length < 2) {
                LOGGER.warn("Error env format! env: {}", envIdAndName);
                continue;
            }
            envNameAndIdMap.put(env[1].replaceFirst(linkisDataSource.getLinkisDataSourceId() + SpecCharEnum.BOTTOM_BAR.getValue(), ""), Long.valueOf(env[0]));
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
     * 1. If the env name the user enters is: env_name
     * 2. then format it to:
     * manual input: 212_env_name
     * automatic input: 212_env_name-0.0.0.0:00(epccmaindb_G-DCN_D21_set_4)
     *
     * @param linkisDataSourceId
     * @param originalEnvName
     * @param inputType
     * @param host
     * @return
     */
    @Override
    public String convertOriginalEnvNameToLinkis(Long linkisDataSourceId, String originalEnvName, Integer inputType, String host, String port, String databaseInstance) {
        StringBuilder linkisEnvName = new StringBuilder();
        linkisEnvName.append(linkisDataSourceId);
        linkisEnvName.append(SpecCharEnum.BOTTOM_BAR.getValue());
        linkisEnvName.append(originalEnvName);
        if (isAutoInput(inputType)) {
            linkisEnvName.append(SpecCharEnum.MINUS.getValue());
            linkisEnvName.append(host);
            linkisEnvName.append(SpecCharEnum.MINUS.getValue());
            linkisEnvName.append(port);
            linkisEnvName.append(SpecCharEnum.LEFT_SMALL_BRACKET.getValue());
            linkisEnvName.append(databaseInstance);
            linkisEnvName.append(SpecCharEnum.RIGHT_SMALL_BRACKET.getValue());
        }
        return linkisEnvName.toString();
    }

    /**
     * 1. If the env name store in database is:
     * linkisEnvName with input manually: 212_env_name
     * linkisEnvName with input automatically: 212_env_name-0.0.0.0-00(epccmaindb_G-DCN_D21_set_4)
     * 2. then recovery it to: env_name
     *
     * @param linkisDataSourceId
     * @param linkisEnvName
     * @param inputType
     * @return
     */
    @Override
    public String convertLinkisEnvNameToOriginal(Long linkisDataSourceId, String linkisEnvName, Integer inputType) {
//        1. 212_env_name-0.0.0.0-00(epccmaindb_G-DCN_D21_set_4)
        String tmpLinkisEnvName = linkisEnvName;
//        removing the prefix of the env_name: 212_
        int prefixIndex = 0;
        String prefixStr = linkisDataSourceId + SpecCharEnum.BOTTOM_BAR.getValue();
        if (-1 != tmpLinkisEnvName.indexOf(prefixStr)) {
            prefixIndex = prefixStr.length();
        }
//        2. env_name-0.0.0.0-00(epccmaindb_G-DCN_D21_set_4)
        tmpLinkisEnvName = StringUtils.substring(tmpLinkisEnvName, prefixIndex);

        if (isAutoInput(inputType)) {
            String[] envSegments = StringUtils.split(tmpLinkisEnvName, SpecCharEnum.MINUS.getValue());
            if (envSegments.length > 0) {
                tmpLinkisEnvName = envSegments[0];
            }
        }
        return tmpLinkisEnvName;
    }

    private boolean isAutoInput(Integer inputType) {
        return Integer.valueOf(QualitisConstants.DATASOURCE_MANAGER_INPUT_TYPE_AUTO).equals(inputType);
    }

}
