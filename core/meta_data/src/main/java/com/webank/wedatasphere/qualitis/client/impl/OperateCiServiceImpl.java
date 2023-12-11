package com.webank.wedatasphere.qualitis.client.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.client.config.MetricPropertiesConfig;
import com.webank.wedatasphere.qualitis.client.config.OperateCiConfig;
import com.webank.wedatasphere.qualitis.client.constant.OperateEnum;
import com.webank.wedatasphere.qualitis.client.request.OperateRequest;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.OperateCiService;
import com.webank.wedatasphere.qualitis.metadata.response.*;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${department.data_source_from: custom}")
    private String departmentSourceType;

    @Value("${deploy.environment: open_source}")
    private String deployEnvType;

    private static final Logger LOGGER = LoggerFactory.getLogger(OperateCiServiceImpl.class);

    @Override
    public List<SubSystemResponse> getAllSubSystemInfo() throws UnExpectedRequestException {
//       仅限开源环境
        if ("open_source".equals(deployEnvType)) {
            return Collections.emptyList();
        }

        Map<String, Object> response = requestCmdb(OperateEnum.SUB_SYSTEM, "A problem occurred when converting the request body to json.", "{&FAILED_TO_GET_SUB_SYSTEM_INFO}", "Start to get sub_system info from cmdb. url: {}, method: {}, body: {}", "Succeed to get sub_system info from cmdb. response.");

        List<Object> content = checkResponse(response);

        List<SubSystemResponse> responses = new ArrayList<>(content.size());

        for (int i = 0; i < content.size(); i++) {
            SubSystemResponse tempResponse = new SubSystemResponse();
            Object current = content.get(i);

            Integer currentSubsystemId = ((Map<String, Integer>) current).get("subsystem_id");
            tempResponse.setSubSystemId(currentSubsystemId);

            String currentSubSystemName = ((Map<String, String>) current).get("subsystem_name");
            tempResponse.setSubSystemName(currentSubSystemName);

            String currentFullCnmName = ((Map<String, String>) current).get("full_cn_name");
            tempResponse.setSubSystemFullCnName(currentFullCnmName);

            List<Map<String, Object>> opsList = (List) ((Map<String, Object>) current).get("pro_oper_group");
            List<Map<String, Object>> deptList = (List) ((Map<String, Object>) current).get("busiResDept");
            List<Map<String, Object>> devList = (List) ((Map<String, Object>) current).get("devdept");

            String dept = "";
            String opsDept = "";
            String devDept = "";

            if (CollectionUtils.isNotEmpty(deptList)) {
                dept = (String) (deptList.iterator().next()).get("v");
                tempResponse.setDepartmentName(dept);
            }

            if (CollectionUtils.isNotEmpty(opsList)) {
                opsDept = (String) (opsList.iterator().next()).get("v");
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
                    tempResponse.setOpsDepartmentName(opsDept.replace(dept + "-", ""));
                }
            }

            if (CollectionUtils.isNotEmpty(devList)) {
                devDept = (String) (devList.iterator().next()).get("v");
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
                    tempResponse.setDevDepartmentName(devDept.replace(dept + "-", ""));
                }
            }

            responses.add(tempResponse);
        }

        return responses;
    }

    private Map<String, Object> requestCmdb(OperateEnum subSystem, String problemDescribe, String international, String requestInfo, String successInfo) throws UnExpectedRequestException {
        String url = UriBuilder.fromUri(operateCiConfig.getHost()).path(operateCiConfig.getUrl()).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        // Construct request body.
        OperateRequest request = new OperateRequest(subSystem.getCode());
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
        int code = (int) ((Map<String, Object>) response.get("headers")).get("retCode");

        List<Object> content = (List<Object>) ((Map<String, Object>) response.get("data")).get("content");
        int size = (int) ((Map<String, Object>) response.get("headers")).get("contentRows");
        LOGGER.info("Num of operate info is: {}", size);

        if (0 == code && content.size() == size) {
            return content;
        } else {
            throw new UnExpectedRequestException("The result of operate info is not correct.");
        }
    }

    @Override
    public List<ProductResponse> getAllProductInfo()
            throws UnExpectedRequestException {
        if ("open_source".equals(deployEnvType)) {
            return Collections.emptyList();
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
        String url = UriBuilder.fromUri(operateCiConfig.getEfHost()).path(operateCiConfig.getEfUrl())
                .queryParam("AppToken", operateCiConfig.getEfAppToken())
                .queryParam("AppId", operateCiConfig.getEfAppId())
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
            if (pId == null || !deptCode.equals(pId)) {
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
    public GeneralResponse<DcnResponse> getDcn(Long subSystemId) throws UnExpectedRequestException {
        String url = UriBuilder.fromUri(operateCiConfig.getHost()).path(operateCiConfig.getIntegrateUrl()).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        // Construct request body.
        OperateRequest request = new OperateRequest(OperateEnum.SUB_SYSTEM_FIND_DCN.getCode());
        request.setUserAuthKey(operateCiConfig.getNewUserAuthKey());
        request.getFilter().put("subsystem_id", subSystemId.toString());
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

        DcnResponse dcnResponse = new DcnResponse((List<Map<String, Object>>) ((Map<String, Object>) response.get("data")).get("content"));

        // Filter MASTER
        if (Boolean.TRUE.equals(operateCiConfig.getOnlySlave())) {
            filterDcn(dcnResponse);
        }

        return new GeneralResponse<>("200", "Success to get dcn by subsystem", dcnResponse);
    }

    private void filterDcn(DcnResponse dcnResponse) {
        Map<Object, Map<Object, List<Map<String, Object>>>> resMap = dcnResponse.getRes();
        Iterator<Map.Entry<Object, Map<Object, List<Map<String, Object>>>>> resIterator = resMap.entrySet().iterator();
        while (resIterator.hasNext()) {
            Map.Entry<Object, Map<Object, List<Map<String, Object>>>> res = resIterator.next();
            Map<Object, List<Map<String, Object>>> dcnMap = res.getValue();
            Iterator<Map.Entry<Object, List<Map<String, Object>>>> dcnIterator = dcnMap.entrySet().iterator();
            while (dcnIterator.hasNext()) {
                List<Map<String, Object>> logicDcns = dcnIterator.next().getValue();
                ListIterator<Map<String, Object>> logicDcnIterator = logicDcns.listIterator();
                while (logicDcnIterator.hasNext()) {
                    Map<String, Object> dcn = logicDcnIterator.next();
                    if ("MASTER".equals(dcn.get("set_type"))) {
                        logicDcnIterator.remove();
                    }
                }
                if (CollectionUtils.isEmpty(logicDcns)) {
                    dcnIterator.remove();
                }
            }
            if (MapUtils.isEmpty(dcnMap)) {
                resIterator.remove();
            }
        }
    }
}
