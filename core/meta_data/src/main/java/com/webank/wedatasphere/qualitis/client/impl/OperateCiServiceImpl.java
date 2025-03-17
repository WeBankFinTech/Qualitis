package com.webank.wedatasphere.qualitis.client.impl;

import cn.hutool.db.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.webank.wedatasphere.qualitis.client.config.MetricPropertiesConfig;
import com.webank.wedatasphere.qualitis.client.constant.OperateEnum;
import com.webank.wedatasphere.qualitis.client.request.OperateRequest;
import com.webank.wedatasphere.qualitis.config.OperateCiConfig;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.OperateCiService;
import com.webank.wedatasphere.qualitis.metadata.response.*;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.util.map.CustomObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author allenzhou@webank.com
 * @date 2021/3/2 10:58
 */
@Service
public class OperateCiServiceImpl implements OperateCiService {
    @Autowired
    private MetricPropertiesConfig metricPropertiesConfig;

    @Autowired
    private OperateCiConfig operateCiConfig;

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(OperateCiServiceImpl.class);

    @Value("${overseas_external_version.enable:false}")
    private Boolean overseasVersionEnabled;

    @Override
    public List<BuzDomainResponse> getAllBuzDomainInfo() throws UnExpectedRequestException, IOException {
        if (overseasVersionEnabled){
            LOGGER.info(" get all buz Domain Info return empty list.");
            return new ArrayList<>();
        }
        String url = UriBuilder.fromUri(operateCiConfig.getHost()).path(operateCiConfig.getIntegrateUrl()).toString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        // Construct request body.
        OperateRequest request = new OperateRequest(OperateEnum.BUZ_DOMAIN.getCode());
        request.setUserAuthKey(operateCiConfig.getNewUserAuthKey());
        HttpEntity<Object> entity = null;
        try {
            String jsonRequest = objectMapper.writeValueAsString(request);
            LOGGER.info("Operate request: {}", jsonRequest);
            entity = new HttpEntity<>(jsonRequest, headers);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new UnExpectedRequestException(e.getMessage());
        }

        Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
        List<Object> appDomainList = checkResponse(response);
        if (CollectionUtils.isEmpty(appDomainList)) {
            LOGGER.warn("Failed to get buzDomainInfo: data is empty.");
        }
        List<BuzDomainResponse> responses = Lists.newArrayListWithCapacity(appDomainList.size());
        for (int i = 0; i < appDomainList.size(); i++) {
            BuzDomainResponse buzDomainResponse = new BuzDomainResponse();
            Object appDomain = appDomainList.get(i);
            if (!(appDomain instanceof Map)) {
                LOGGER.warn("Error data format. original content: {}", CustomObjectMapper.transObjectToJson(appDomain));
                break;
            }
            buzDomainResponse.setName(MapUtils.getString((Map<String, Object>)appDomain, "appdomain_cnname"));
            responses.add(buzDomainResponse);
        }

        return responses;
    }

    @Override
    public List<SubSystemResponse> getAllSubSystemInfo() throws UnExpectedRequestException {
        if (overseasVersionEnabled){
            LOGGER.info(" get all sub system info return default 5375.");
            SubSystemResponse subSystemResponse = new SubSystemResponse();
            subSystemResponse.setSubSystemId("5375");
            subSystemResponse.setSubSystemName("WDSDQMS-CORE");
            subSystemResponse.setSubSystemFullCnName("数据质量管理服务");

            List<SubSystemResponse> subSystemResponses = new ArrayList<>();
            subSystemResponses.add(subSystemResponse);
            return subSystemResponses;
        }
        Map<String, Object> response = requestCmdb(OperateEnum.SUB_SYSTEM, "A problem occurred when converting the request body to json.", "{&FAILED_TO_GET_SUB_SYSTEM_INFO}", "Start to get sub_system info from cmdb. url: {}, method: {}, body: {}", "Succeed to get sub_system info from cmdb. response.", null, null);

        List<Object> content = checkResponse(response);

        List<SubSystemResponse> responses = new ArrayList<>(content.size());

        for (int i = 0; i < content.size(); i++) {
            SubSystemResponse tempResponse = new SubSystemResponse();
            Object current = content.get(i);
            if (!(current instanceof Map)) {
                LOGGER.warn("Error data format. original content: {}", CustomObjectMapper.transObjectToJson(current));
                break;
            }
            Map<String, Object> currentMap = (Map<String, Object>) current;

            String currentSubsystemId = null;
            try {
                currentSubsystemId = currentMap.get("subsystem_id").toString();
            } catch (Exception e) {
                LOGGER.warn("Current subsystem ID cannot be number. error: {}", e.getMessage());
            }
            tempResponse.setSubSystemId(currentSubsystemId);

            String currentSubSystemName = MapUtils.getString(currentMap, "subsystem_name");
            tempResponse.setSubSystemName(currentSubSystemName);

            String currentFullCnmName = MapUtils.getString(currentMap, "full_cn_name");
            tempResponse.setSubSystemFullCnName(currentFullCnmName);

            List<Map<String, Object>> opsList = (List) currentMap.get("pro_oper_group");
            List<Map<String, Object>> deptList = (List) currentMap.get("busiResDept");
            List<Map<String, Object>> devList = (List) currentMap.get("devdept");

            String dept = "";
            String opsDept = "";
            String devDept = "";

            try {
                if (CollectionUtils.isNotEmpty(deptList)) {
                    dept = (String) (deptList.iterator().next()).getOrDefault("v", "");
                    tempResponse.setDepartmentName(dept);
                }

                if (CollectionUtils.isNotEmpty(opsList)) {
                    opsDept = (String) (opsList.iterator().next()).getOrDefault("v", "");
                    if (StringUtils.isEmpty(dept)) {
                        String[] infos = opsDept.split(SpecCharEnum.MINUS.getValue());
                        if (infos.length == 2) {
                            dept = infos[0];
                            tempResponse.setDepartmentName(dept);
                            tempResponse.setOpsDepartmentName(infos[1]);
                        } else {
                            tempResponse.setOpsDepartmentName(infos[0]);
                        }
                    } else {
                        tempResponse.setOpsDepartmentName(opsDept.replace(StringUtils.trimToEmpty(dept) + "-", ""));
                    }
                }

                if (CollectionUtils.isNotEmpty(devList)) {
                    devDept = (String) (devList.iterator().next()).getOrDefault("v", "");
                    if (StringUtils.isEmpty(dept)) {
                        String[] infos = devDept.split(SpecCharEnum.MINUS.getValue());
                        if (infos.length == 2) {
                            dept = infos[0];
                            tempResponse.setDepartmentName(dept);
                            tempResponse.setDevDepartmentName(infos[1]);
                        } else {
                            tempResponse.setDevDepartmentName(infos[0]);
                        }
                    } else {
                        tempResponse.setDevDepartmentName(devDept.replace(StringUtils.trimToEmpty(dept) + "-", ""));
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Failed to format data: {}", CustomObjectMapper.transObjectToJson(current), e );
            }
            responses.add(tempResponse);
        }

        return responses;
    }

    @Override
    public List<SubSystemResponse> getSubSystemInfoByPage(String subSystemName, int page, int size) throws UnExpectedRequestException {
        if (overseasVersionEnabled){
            LOGGER.info(" get usb system Info return default 5375.");
            SubSystemResponse subSystemResponse = new SubSystemResponse();
            subSystemResponse.setSubSystemId("5375");
            subSystemResponse.setSubSystemName("WDSDQMS-CORE");
            subSystemResponse.setSubSystemFullCnName("数据质量管理服务");

            List<SubSystemResponse> subSystemResponses = new ArrayList<>();
            subSystemResponses.add(subSystemResponse);
            return subSystemResponses;
        }
        Map<String, String> filter = null;
        if (StringUtils.isNotBlank(subSystemName)) {
            filter = Maps.newHashMapWithExpectedSize(1);
            filter.put("subsystem_name", subSystemName);
        }

        Page pageRequest = new Page();
        pageRequest.setPageNumber(page);
        pageRequest.setPageSize(size);
        Map<String, Object> response = requestCmdb(OperateEnum.SUB_SYSTEM, "A problem occurred when converting the request body to json.", "{&FAILED_TO_GET_SUB_SYSTEM_INFO}", "Start to get sub_system info from cmdb. url: {}, method: {}, body: {}", "Succeed to get sub_system info from cmdb. response.", filter, pageRequest);

        List<Object> content = checkResponse(response);

        List<SubSystemResponse> responses = new ArrayList<>(content.size());
        for (int i = 0; i < content.size(); i++) {
            SubSystemResponse tempResponse = new SubSystemResponse();
            Object current = content.get(i);
            if (!(current instanceof Map)) {
                LOGGER.warn("Error data format. original content: {}", CustomObjectMapper.transObjectToJson(current));
                break;
            }
            Map<String, Object> currentMap = (Map<String, Object>) current;

            String currentSubsystemId = null;
            try {
                currentSubsystemId = currentMap.get("subsystem_id").toString();
            } catch (Exception e) {
                LOGGER.warn("Current subsystem ID cannot be number. error: {}", e.getMessage());
            }
            tempResponse.setSubSystemId(currentSubsystemId);

            String currentSubSystemName = MapUtils.getString(currentMap, "subsystem_name");
            tempResponse.setSubSystemName(currentSubSystemName);

            String currentFullCnmName = MapUtils.getString(currentMap, "full_cn_name");
            tempResponse.setSubSystemFullCnName(currentFullCnmName);

            List<Map<String, Object>> opsList = (List) currentMap.get("pro_oper_group");
            List<Map<String, Object>> deptList = (List) currentMap.get("busiResDept");
            List<Map<String, Object>> devList = (List) currentMap.get("devdept");

            String dept = "";
            String opsDept = "";
            String devDept = "";

            try {
                if (CollectionUtils.isNotEmpty(deptList)) {
                    dept = (String) (deptList.iterator().next()).getOrDefault("v", "");
                    tempResponse.setDepartmentName(dept);
                }

                if (CollectionUtils.isNotEmpty(opsList)) {
                    opsDept = (String) (opsList.iterator().next()).getOrDefault("v", "");
                    if (StringUtils.isEmpty(dept)) {
                        String[] infos = opsDept.split(SpecCharEnum.MINUS.getValue());
                        if (infos.length == 2) {
                            dept = infos[0];
                            tempResponse.setDepartmentName(dept);
                            tempResponse.setOpsDepartmentName(infos[1]);
                        } else {
                            tempResponse.setOpsDepartmentName(infos[0]);
                        }
                    } else {
                        tempResponse.setOpsDepartmentName(opsDept.replace(StringUtils.trimToEmpty(dept) + "-", ""));
                    }
                }

                if (CollectionUtils.isNotEmpty(devList)) {
                    devDept = (String) (devList.iterator().next()).getOrDefault("v", "");
                    if (StringUtils.isEmpty(dept)) {
                        String[] infos = devDept.split(SpecCharEnum.MINUS.getValue());
                        if (infos.length == 2) {
                            dept = infos[0];
                            tempResponse.setDepartmentName(dept);
                            tempResponse.setDevDepartmentName(infos[1]);
                        } else {
                            tempResponse.setDevDepartmentName(infos[0]);
                        }
                    } else {
                        tempResponse.setDevDepartmentName(devDept.replace(StringUtils.trimToEmpty(dept) + "-", ""));
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Failed to format data: {}", CustomObjectMapper.transObjectToJson(current), e );
            }
            responses.add(tempResponse);
        }

        return responses;
    }

    @Override
    public String getSubSystemIdByName(String subSystemName) throws UnExpectedRequestException {
        if (overseasVersionEnabled){
            LOGGER.info(" get sub system id return null.");
            //上层调用判断了非空
            return null;
        }
        Map<String, Object> response = requestCmdb(OperateEnum.SUB_SYSTEM, "A problem occurred when converting the request body to json.", "{&FAILED_TO_GET_SUB_SYSTEM_INFO}", "Start to get sub_system info from cmdb. url: {}, method: {}, body: {}", "Succeed to get sub_system info from cmdb. response.", null, null);

        List<Object> content = checkResponse(response);
        for (int i = 0; i < content.size(); i++) {
            Object current = content.get(i);
            String currentSubSystemName = ((Map<String, String>) current).get("subsystem_name");
            if (!subSystemName.equals(currentSubSystemName)) {
                continue;
            }

            String currentSubsystemId;
            try {
                currentSubsystemId = ((Map<String, Integer>) current).get("subsystem_id").toString();
            } catch (Exception e1) {
                try {
                    currentSubsystemId = ((Map<String, String>) current).get("subsystem_id");
                } catch (Exception e2) {
                    LOGGER.warn("Current subsystem ID cannot be number.");
                    continue;
                }
            }
            if (null != currentSubsystemId) {
                return currentSubsystemId;
            }
        }
        return null;
    }

    private Map<String, Object> requestCmdb(OperateEnum subSystem, String problemDescribe, String international, String requestInfo, String successInfo, Map<String, String> filter, Page pageRequest) throws UnExpectedRequestException {
        if (overseasVersionEnabled){
            LOGGER.info(" request cmdb return empty map.");
            return new HashMap<>();
        }
        String url = UriBuilder.fromUri(operateCiConfig.getHost()).path(operateCiConfig.getUrl()).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        // Construct request body.
        OperateRequest request = new OperateRequest(subSystem.getCode());
        if (null != pageRequest) {
            request.setPaging(true);
            request.setPageSize(pageRequest.getPageSize());
            request.setStartIndex(pageRequest.getPageNumber() * pageRequest.getPageSize());
        }
        if (null != filter) {
            request.setFilter(filter);
        }

        request.setUserAuthKey(operateCiConfig.getUserAuthKey());
        HttpEntity<Object> entity = null;
        try {
            String jsonRequest = objectMapper.writeValueAsString(request);
            LOGGER.info("Operate request: {}", jsonRequest);
            entity = new HttpEntity<>(jsonRequest, headers);
        } catch (IOException e) {
            LOGGER.error(problemDescribe);
            throw new UnExpectedRequestException(international);
        }
        LOGGER.info(requestInfo, url, javax.ws.rs.HttpMethod.POST, entity);
        Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
        LOGGER.info(successInfo);
        return response;
    }

    private List<Object> checkResponse(Map<String, Object> response)
            throws UnExpectedRequestException {
        if (MapUtils.isEmpty(response)) {
            throw new UnExpectedRequestException("Failed to get the result of operate info, response is empty. ");
        }
        if (!response.containsKey("headers")) {
            throw new UnExpectedRequestException("Failed to get the result of operate info, miss required arguments. response: " + MapUtils.getString(response, "msg"));
        }
        int code = (int) ((Map<String, Object>) response.get("headers")).get("retCode");
        if (0 != code) {
            String errorInfo = MapUtils.getString(((Map<String, Object>) response.get("headers")), "errorInfo");
            throw new UnExpectedRequestException(errorInfo);
        }

        List<Object> content = (List<Object>) ((Map<String, Object>) response.get("data")).get("content");
        int size = (int) ((Map<String, Object>) response.get("headers")).get("contentRows");
        LOGGER.info("Num of operate info is: {}", size);

        if (content.size() != size) {
            throw new UnExpectedRequestException("The result of operate info is not correct.");
        }

        return content;
    }

    @Override
    public List<ProductResponse> getAllProductInfo()
            throws UnExpectedRequestException {
        if (overseasVersionEnabled){
            LOGGER.info(" get all product Info return Qualitis.");
            ProductResponse productResponse = new ProductResponse();
            productResponse.setProductId("Qualitis");
            productResponse.setProductName("Qualitis");

            List<ProductResponse> productResponses = new ArrayList<>();
            productResponses.add(productResponse);
            return productResponses;
        }
        String url = UriBuilder.fromUri(operateCiConfig.getHost()).path(operateCiConfig.getUrl()).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        // Construct request body.
        OperateRequest request = new OperateRequest(OperateEnum.PRODUCT.getCode());
        request.setUserAuthKey(operateCiConfig.getUserAuthKey());
        HttpEntity<Object> entity = null;
        try {
            String jsonRequest = objectMapper.writeValueAsString(request);
            LOGGER.info("Operate request: {}", jsonRequest);
            entity = new HttpEntity<>(jsonRequest, headers);
        } catch (IOException e) {
            LOGGER.error("A problem occurred when converting the request body to json. ");
            throw new UnExpectedRequestException("{&FAILED_TO_GET_PRODUCT_INFO}");
        }
        LOGGER.info("Start to get product info from cmdb. url: {}, method: {}, body: {}", url, javax.ws.rs.HttpMethod.POST, entity);
        Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
        LOGGER.info("Succeed to get product info from cmdb. response.");

        List<Object> content = checkResponse(response);

        List<ProductResponse> responses = new ArrayList<>(content.size());
        for (int i = 0; i < content.size(); i++) {
            ProductResponse tempResponse = new ProductResponse();
            Object current = content.get(i);

            String currentProductId = ((Map<String, String>) current).get("product_cd");
            tempResponse.setProductId(currentProductId);

            String currentProductName = ((Map<String, String>) current).get("cn_name");
            tempResponse.setProductName(currentProductName);

            responses.add(tempResponse);
        }

        return responses;
    }

    @Override
    public List<CmdbDepartmentResponse> getAllDepartmetInfo() throws UnExpectedRequestException {
        if (overseasVersionEnabled){
            LOGGER.info(" get all department Info return empty list.");
            return new ArrayList<>();
        }
        Integer pId = 100000;
        LOGGER.info("Start to get department info from esb. PID: {}", pId);
        List<DepartmentSubResponse> departmentSubResponses = getDevAndOpsInfo(pId);
        LOGGER.info("Succeed to get department response from esb.");

        List<CmdbDepartmentResponse> responses = Lists.newArrayListWithCapacity(departmentSubResponses.size());
        List<String> departmentList = Arrays.asList(metricPropertiesConfig.getWhiteList().split(SpecCharEnum.COMMA.getValue()));

        for (int i = 0; i < departmentSubResponses.size(); i++) {
            DepartmentSubResponse current = departmentSubResponses.get(i);

            String departmentCode = current.getId();
            String departmentName = current.getName();
            LOGGER.info("The [{}]-th department name is [{}]", i, departmentName);
            CmdbDepartmentResponse cmdbDepartmentResponse = new CmdbDepartmentResponse();
            cmdbDepartmentResponse.setCode(departmentCode);
            cmdbDepartmentResponse.setName(departmentName);
            if (departmentList.contains(departmentName)) {
                cmdbDepartmentResponse.setDisable("1");
            } else {
                cmdbDepartmentResponse.setDisable("0");
            }

            responses.add(cmdbDepartmentResponse);
        }

        return responses;
    }

    @Override
    public List<DepartmentSubResponse> getDevAndOpsInfo(Integer deptCode) throws UnExpectedRequestException {
        if (overseasVersionEnabled){
            LOGGER.info(" get dev and ops info return empty list.");
            return new ArrayList<>();
        }
        String url = UriBuilder.fromUri(operateCiConfig.getEfHost()).path(operateCiConfig.getEfUrl())
                .queryParam("AppToken", operateCiConfig.getEfAppToken())
                .queryParam("AppId", operateCiConfig.getEfAppId())
                .queryParam("status", "A")
                .toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        Map<String, Object> response;
        try {
            LOGGER.info("Start to get dev and ops info from ef. url: {}, method: {}, body: {}", url, HttpMethod.GET, entity);
            response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UnExpectedRequestException("{&FAILED_TO_GET_DEPARTMENT_INFO}");
        }
        LOGGER.info("Finish to get dev and ops info from ef. url: {}, method: {}, body: {}", url, HttpMethod.GET, entity);
        Integer responseCode = (Integer) response.get("Code");
        if (responseCode == null || !responseCode.equals(0)) {
            throw new UnExpectedRequestException("The result of dev and ops info from ef is not correct.");
        }

        LOGGER.info("Success to get dev and ops info from ef. url: {}, method: {}, body: {}", url, HttpMethod.GET, entity);
        List<Map<String, Object>> content = (List<Map<String, Object>>) ((Map<String, Object>) response.get("Result")).get("Data");
        List<DepartmentSubResponse> responses = new ArrayList<>(128);
        for (int i = 0; i < content.size(); i++) {
            DepartmentSubResponse departmentSubResponse = new DepartmentSubResponse();
            Map<String, Object> current = content.get(i);
            Integer pId = (Integer) current.get("PID");
            if (pId == null) {
                continue;
            }
            if (!Integer.valueOf(100000).equals(deptCode) && !deptCode.equals(pId)) {
                continue;
            }

//            WBK_DEPT_LVL: 1-公司，2-条线，3-部门，4-科室，5-群
            String deptLevel = Integer.valueOf(100000).equals(deptCode) ? "3": "4";
            String wbkDeptLvl = (String) current.get("WBK_DEPT_LVL");
            if (!deptLevel.equals(wbkDeptLvl)) {
                continue;
            }

            String orgName = (String) current.get("OrgName");
            Object id = current.get("ID");
            departmentSubResponse.setId(id.toString());
            departmentSubResponse.setName(orgName);
            responses.add(departmentSubResponse);
        }

        return responses;
    }

    @Override
    public GeneralResponse getDcn(String subSystemId, String dcnRangeType, List<String> dcnRangeValues) throws UnExpectedRequestException {
        if (overseasVersionEnabled){
            LOGGER.info(" get dev and ops info return empty list.");
            return new GeneralResponse();
        }
        String url = UriBuilder.fromUri(operateCiConfig.getHost()).path(operateCiConfig.getIntegrateUrl()).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        // Construct request body.
        OperateRequest request = new OperateRequest(OperateEnum.SUB_SYSTEM_FIND_DCN.getCode());
        request.setUserAuthKey(operateCiConfig.getNewUserAuthKey());
        request.getFilter().put("subsystem_id", subSystemId);
        HttpEntity<Object> entity;
        try {
            String jsonRequest = objectMapper.writeValueAsString(request);
            LOGGER.info("Operate request: {}", jsonRequest);
            entity = new HttpEntity<>(jsonRequest, headers);
        } catch (IOException e) {
            LOGGER.error("Failed to get dcn by subsystem.");
            throw new UnExpectedRequestException("Failed to get dcn by subsystem.");
        }
        LOGGER.info("Start to get dcn by subsystem.", url, javax.ws.rs.HttpMethod.POST, entity);
        Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
        LOGGER.info("Finished to get dcn by subsystem.");
        List<Object> content = checkResponse(response);

        filterDcn(content, dcnRangeType, dcnRangeValues);

        if(Arrays.asList(QualitisConstants.CMDB_KEY_DCN_NUM, QualitisConstants.CMDB_KEY_LOGIC_AREA)
                .contains(dcnRangeType)) {
            Map<Object, List<Map<String, Object>>> res = content.stream().map(item -> (Map<String, Object>)item )
                    .collect(Collectors.groupingBy(
                            map -> map.get(dcnRangeType)
                    ));
            return new GeneralResponse(ResponseStatusConstants.OK, "Success to get dcn by subsystem", res);
        } else {
            return new GeneralResponse(ResponseStatusConstants.OK, "Success to get dcn by subsystem", content);
        }
    }

    private void filterDcn(List<Object> maps, String dcnRangeType, List<String> dcnRangeValues) {
        ListIterator<Object> dcnIterator = maps.listIterator();
        while (dcnIterator.hasNext()) {
            Map<String, Object> dcn = (Map<String, Object>) dcnIterator.next();
            if (Boolean.TRUE.equals(operateCiConfig.getOnlySlave()) && "MASTER".equals(dcn.get("set_type"))) {
                dcnIterator.remove();
            }
            if (CollectionUtils.isNotEmpty(dcnRangeValues) && !dcnRangeValues.contains(dcn.get(dcnRangeType))) {
                dcnIterator.remove();
            }
        }

    }

}
