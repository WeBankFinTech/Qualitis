package com.webank.wedatasphere.qualitis.client.impl;

import com.google.gson.Gson;
import com.webank.wedatasphere.qualitis.client.config.DataMapConfig;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.DataStandardClient;
import com.webank.wedatasphere.qualitis.metadata.constant.DataMapResponseKeyEnum;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * @author allenzhou@webank.com
 * @date 2021/6/15 15:29
 */
@Component
public class DataStandardClientImpl implements DataStandardClient {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private DataMapConfig dataMapConfig;
    @Autowired
    private ClusterInfoDao clusterInfoDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(DataStandardClientImpl.class);

    @Override
    public Map<String, Object> getDatabase(String searchKey, String loginUser) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        UriBuilder uriBuilder = UriBuilder.fromUri(dataMapConfig.getAddress())
                .path(dataMapConfig.getDatabasePath())
                .queryParam("searchKey", searchKey)
                .queryParam("isolateEnvFlag", dataMapConfig.getIsolateEnvFlag());
        String url = constructUrlWithSignature(uriBuilder, loginUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add("isAuth", String.valueOf(false));

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get db by datamap. url: {}, method: {}, body: {}", url, HttpMethod.GET, entity);
        String responseStr = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
        LOGGER.info("Finish to get db by datamap. response: {}", responseStr);
        Map<String, Object> response = new Gson().fromJson(responseStr, Map.class);
        if (HttpStatus.SC_OK != Integer.parseInt((String) response.get(DataMapResponseKeyEnum.CODE.getKey()))) {
            String message = (String) response.get("msg");
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from DataMap, message: " + message, 403);
        }

        return (Map<String, Object>) response.get(DataMapResponseKeyEnum.DATA.getKey());
    }

    @Override
    public Map<String, Object> getDataset(String dbId, String datasetName, int page, int size, String loginUser)
            throws UnExpectedRequestException, MetaDataAcquireFailedException {
        UriBuilder uriBuilder = UriBuilder.fromUri(dataMapConfig.getAddress())
                .path(dataMapConfig.getTablePath())
                .queryParam("isolateEnvFlag", dataMapConfig.getIsolateEnvFlag())
                .queryParam("dbId", dbId).queryParam("datasetName", datasetName)
                .queryParam("pageNum", page).queryParam("pageSize", size);
        String url = constructUrlWithSignature(uriBuilder, loginUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add("isAuth", String.valueOf(false));

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get table by datamap. url: {}, method: {}, body: {}", url, HttpMethod.GET, entity);
        String responseStr = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
        LOGGER.info("Finish to get table by datamap. response: {}", responseStr);
        Map<String, Object> response = new Gson().fromJson(responseStr, Map.class);
        if (HttpStatus.SC_OK != Integer.parseInt((String) response.get(DataMapResponseKeyEnum.CODE.getKey()))) {
            String message = (String) response.get("msg");
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from DataMap, exception: " + message, 200);
        }

        return (Map<String, Object>) response.get(DataMapResponseKeyEnum.DATA.getKey());
    }

    @Override
    public Map<String, Object> getColumnStandard(Long datasetId, String fieldName, String loginUser) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        UriBuilder uriBuilder = UriBuilder.fromUri(dataMapConfig.getAddress())
                .path(dataMapConfig.getColumnPath())
                .queryParam("isolateEnvFlag", dataMapConfig.getIsolateEnvFlag())
                .queryParam("datasetId", datasetId)
                .queryParam("fieldName", fieldName)
                .queryParam("pageNum", 0)
                .queryParam("pageSize", 1)
                .queryParam("comment", "")
                .queryParam("contentName", "")
                .queryParam("scLevel", "");
        String url = constructUrlWithSignature(uriBuilder, loginUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add("isAuth", String.valueOf(false));

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get field by datamap. url: {}, method: {}, body: {}", url, HttpMethod.GET, entity);
        String responseStr = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
        LOGGER.info("Finish to get field by datamap. response: {}", responseStr);
        Map<String, Object> response = new Gson().fromJson(responseStr, Map.class);
        if (HttpStatus.SC_OK != Integer.parseInt((String) response.get(DataMapResponseKeyEnum.CODE.getKey()))) {
            String message = (String) response.get("message");
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from DataMap, exception: " + message, 200);
        }

        return (Map<String, Object>) response.get(DataMapResponseKeyEnum.DATA.getKey());
    }

    @Override
    public Map<String, Object> getDataStandardDetail(String stdCode, String source, String loginUser) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        UriBuilder uriBuilder = UriBuilder.fromUri(dataMapConfig.getAddress())
                .path(dataMapConfig.getStandardPath())
                .queryParam("isolateEnvFlag", dataMapConfig.getIsolateEnvFlag())
                .queryParam("stdCode", stdCode)
                .queryParam("source", source);
        String url = constructUrlWithSignature(uriBuilder, loginUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add("isAuth", String.valueOf(false));

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get standard by datamap. url: {}, method: {}, body: {}", url, HttpMethod.GET, entity);
        String responseStr = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
        LOGGER.info("Finish to get standard by datamap. response: {}", responseStr);
        Map<String, Object> response = new Gson().fromJson(responseStr, Map.class);
        if (HttpStatus.SC_OK != Integer.parseInt((String) response.get(DataMapResponseKeyEnum.CODE.getKey()))) {
            String message = (String) response.get("message");
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from DataMap, exception: " + message, 200);
        }

        return (Map<String, Object>) response.get(DataMapResponseKeyEnum.DATA.getKey());
    }

    @Override
    public Map<String, Object> getDataStandardCategory(int page, int size, String loginUser, String stdSubName) throws MetaDataAcquireFailedException, UnExpectedRequestException {
        UriBuilder uriBuilder = UriBuilder.fromUri(dataMapConfig.getAddress())
                .path(dataMapConfig.getDataStandardCategory())
                .queryParam("isolateEnvFlag", dataMapConfig.getIsolateEnvFlag())
                .queryParam("stdSubName", StringUtils.isNotBlank(stdSubName) ? stdSubName : "")
                .queryParam("pageNum", page)
                .queryParam("pageSize", size);
        String url = constructUrlWithSignature(uriBuilder, loginUser);

        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UnExpectedRequestException("Decode get path url exception", 500);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("isAuth", String.valueOf(false));

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get data standard category by datamap. url: {}, method: {}, body: {}", url, HttpMethod.GET, entity);
        String responseStr = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
        LOGGER.info("Finish to get data standard category by datamap. response: {}", responseStr);
        Map<String, Object> response = new Gson().fromJson(responseStr, Map.class);
        if (HttpStatus.SC_OK != Integer.parseInt((String) response.get(DataMapResponseKeyEnum.CODE.getKey()))) {
            String message = (String) response.get("message");
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from DataMap, exception: " + message, 200);
        }

        return (Map<String, Object>) response.get(DataMapResponseKeyEnum.DATA.getKey());
    }

    @Override
    public Map<String, Object> getDataStandardBigCategory(int page, int size, String loginUser, String stdSubName, String stdBigCategoryName) throws MetaDataAcquireFailedException, UnExpectedRequestException {
        UriBuilder uriBuilder = UriBuilder.fromUri(dataMapConfig.getAddress())
                .path(dataMapConfig.getDataStandardBigCategory())
                .queryParam("isolateEnvFlag", dataMapConfig.getIsolateEnvFlag())
                .queryParam("stdSubName", StringUtils.isNotBlank(stdSubName) ? stdSubName : "")
                .queryParam("stdBigCategoryName", StringUtils.isNotBlank(stdBigCategoryName) ? stdBigCategoryName : "")
                .queryParam("pageNum", page)
                .queryParam("pageSize", size);
        String url = constructUrlWithSignature(uriBuilder, loginUser);

        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UnExpectedRequestException("Decode get path url exception", 500);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("isAuth", String.valueOf(false));

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get data standard big category by datamap. url: {}, method: {}, body: {}", url, HttpMethod.GET, entity);
        String responseStr = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
        LOGGER.info("Finish to get data standard big category by datamap. response: {}", responseStr);
        Map<String, Object> response = new Gson().fromJson(responseStr, Map.class);
        if (HttpStatus.SC_OK != Integer.parseInt((String) response.get(DataMapResponseKeyEnum.CODE.getKey()))) {
            String message = (String) response.get("message");
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from DataMap, exception: " + message, 200);
        }

        return (Map<String, Object>) response.get(DataMapResponseKeyEnum.DATA.getKey());
    }

    @Override
    public Map<String, Object> getDataStandardSmallCategory(int page, int size, String loginUser, String stdSubName, String stdBigCategoryName, String smallCategoryName) throws MetaDataAcquireFailedException, UnExpectedRequestException {
        UriBuilder uriBuilder = UriBuilder.fromUri(dataMapConfig.getAddress())
                .path(dataMapConfig.getDataStandardSmallCategory())
                .queryParam("isolateEnvFlag", dataMapConfig.getIsolateEnvFlag())
                .queryParam("stdSubName", StringUtils.isNotBlank(stdSubName) ? stdSubName : "")
                .queryParam("stdBigCategoryName", StringUtils.isNotBlank(stdBigCategoryName) ? stdBigCategoryName : "")
                .queryParam("smallCategoryName", StringUtils.isNotBlank(smallCategoryName) ? smallCategoryName : "")
                .queryParam("pageNum", page)
                .queryParam("pageSize", size);
        String url = constructUrlWithSignature(uriBuilder, loginUser);

        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UnExpectedRequestException("Decode get path url exception", 500);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("isAuth", String.valueOf(false));

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get data standard small category by datamap. url: {}, method: {}, body: {}", url, HttpMethod.GET, entity);
        String responseStr = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
        LOGGER.info("Finish to get data standard small category by datamap. response: {}", responseStr);
        Map<String, Object> response = new Gson().fromJson(responseStr, Map.class);
        if (HttpStatus.SC_OK != Integer.parseInt((String) response.get(DataMapResponseKeyEnum.CODE.getKey()))) {
            String message = (String) response.get("message");
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from DataMap, exception: " + message, 200);
        }

        return (Map<String, Object>) response.get(DataMapResponseKeyEnum.DATA.getKey());
    }

    @Override
    public Map<String, Object> getDataStandard(int page, int size, String loginUser, String stdSmallCategoryUrn, String stdCnName) throws MetaDataAcquireFailedException, UnExpectedRequestException, URISyntaxException {
        UriBuilder uriBuilder = UriBuilder.fromUri(dataMapConfig.getAddress())
                .path(dataMapConfig.getDataStandardUrnPath())
                .queryParam("isolateEnvFlag", dataMapConfig.getIsolateEnvFlag())
                .queryParam("stdSmallCategoryUrn", stdSmallCategoryUrn)
                .queryParam("stdCnName", StringUtils.isNotBlank(stdCnName) ? stdCnName : "")
                .queryParam("pageNum", page)
                .queryParam("pageSize", size);
        String url = constructUrlWithSignature(uriBuilder, loginUser);

        String replaceUrl = url.replace("\"", "%22");

        try {
            replaceUrl = URLDecoder.decode(replaceUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UnExpectedRequestException("Decode get path url exception", 500);
        }

        URI uri = new URI(replaceUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.add("isAuth", String.valueOf(false));

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get data standard by datamap. url: {}, method: {}, body: {}", url, HttpMethod.GET, entity);
        String responseStr = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class).getBody();
        LOGGER.info("Finish to get data standard by datamap. response: {}", responseStr);
        Map<String, Object> response = new Gson().fromJson(responseStr, Map.class);
        if (HttpStatus.SC_OK != Integer.parseInt((String) response.get(DataMapResponseKeyEnum.CODE.getKey()))) {
            String message = (String) response.get("message");
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from DataMap, exception: " + message, 200);
        }

        return (Map<String, Object>) response.get(DataMapResponseKeyEnum.DATA.getKey());
    }

    @Override
    public Map<String, Object> getStandardCode(int page, int size, String loginUser, String stdUrn) throws MetaDataAcquireFailedException, UnExpectedRequestException, URISyntaxException {
        UriBuilder uriBuilder = UriBuilder.fromUri(dataMapConfig.getAddress())
                .path(dataMapConfig.getDataStandardCodePath())
                .queryParam("isolateEnvFlag", dataMapConfig.getIsolateEnvFlag())
                .queryParam("stdUrn", stdUrn)
                .queryParam("pageNum", page)
                .queryParam("pageSize", size);
        String url = constructUrlWithSignature(uriBuilder, loginUser);
        String replaceUrl = url.replace("\"", "%22");
        URI uri = new URI(replaceUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.add("isAuth", String.valueOf(false));

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get  standard code by datamap. url: {}, method: {}, body: {}", url, HttpMethod.GET, entity);
        String responseStr = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class).getBody();
        LOGGER.info("Finish to get standard code by datamap. response: {}", responseStr);
        Map<String, Object> response = new Gson().fromJson(responseStr, Map.class);
        if (HttpStatus.SC_OK != Integer.parseInt((String) response.get(DataMapResponseKeyEnum.CODE.getKey()))) {
            String message = (String) response.get("message");
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from DataMap, exception: " + message, 200);
        }

        return (Map<String, Object>) response.get(DataMapResponseKeyEnum.DATA.getKey());
    }

    @Override
    public Map<String, Object> getStandardCodeTable(int page, int size, String loginUser, String stdCode) throws MetaDataAcquireFailedException, UnExpectedRequestException {
        UriBuilder uriBuilder = UriBuilder.fromUri(dataMapConfig.getAddress())
                .path(dataMapConfig.getDataStandardCodeTable())
                .queryParam("stdCode", StringUtils.isNotBlank(stdCode) ? stdCode : "")
                .queryParam("pageNum", page)
                .queryParam("pageSize", size);
        String url = constructUrlWithSignature(uriBuilder, loginUser);

        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UnExpectedRequestException("Decode get path url exception", 500);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("isAuth", String.valueOf(false));

        HttpEntity<Object> entity = new HttpEntity<>(headers);
        LOGGER.info("Start to get  standard code table by datamap. url: {}, method: {}, body: {}", url, HttpMethod.GET, entity);
        String responseStr = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
        LOGGER.info("Finish to get standard code table by datamap. response: {}", responseStr);
        Map<String, Object> response = new Gson().fromJson(responseStr, Map.class);
        if (HttpStatus.SC_OK != Integer.parseInt((String) response.get(DataMapResponseKeyEnum.CODE.getKey()))) {
            String message = (String) response.get("message");
            throw new MetaDataAcquireFailedException("Error! Can not get meta data from DataMap, exception: " + message, 200);
        }

        return (Map<String, Object>) response.get(DataMapResponseKeyEnum.DATA.getKey());
    }

    private String constructUrlWithSignature(UriBuilder url, String loginUser) throws UnExpectedRequestException {
        String nonce = UuidGenerator.generateRandom(5);
        String timestamp = String.valueOf(System.currentTimeMillis());

        String signature = hashWithDataMap(hashWithDataMap(dataMapConfig.getAppId() + nonce + loginUser + timestamp) + dataMapConfig.getAppToken());

        return url.queryParam("appid", dataMapConfig.getAppId()).queryParam("nonce", nonce).queryParam("timestamp", timestamp)
                .queryParam("loginUser", loginUser).queryParam("signature", signature).toString();
    }

    private String hashWithDataMap(String str) throws UnExpectedRequestException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UnExpectedRequestException("A error occured when pick up a algorithm of hash to construct datamap http request.", 500);
        }
        md.update(str.getBytes());
        byte[] digest = md.digest();
        String hashStr = DatatypeConverter.printHexBinary(digest).toLowerCase();
        return hashStr;
    }

}
