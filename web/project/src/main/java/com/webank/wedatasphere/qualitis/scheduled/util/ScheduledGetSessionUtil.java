package com.webank.wedatasphere.qualitis.scheduled.util;

import com.google.gson.Gson;
import com.webank.wedatasphere.qualitis.constants.ThirdPartyConstants;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
@Component
public class ScheduledGetSessionUtil {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledGetSessionUtil.class);

    public static String encrypt(String str, String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        byte[] decoded = Base64.getDecoder().decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        Cipher cipher = Cipher.getInstance(ThirdPartyConstants.WTSS_RSA);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }

    private static String getScheduledResultByRealUser(String realUser, String clusterName) throws UnExpectedRequestException, IOException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException, URISyntaxException {
        Map<String, Object> map = findParameterConfiguration(clusterName);

        String token = encrypt(map.get("loginAccount").toString(), map.get("publickey").toString());
        //username放运维用户，normalUserName放实名用户
        String url = String.format("%s/checkin?action=login&username=%s&userpwd=&common_secret=%s",map.get("path").toString(),realUser, token);
        URI uri = new URI(url);
        logger.info("发起请求 url: " + url);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(headers), String.class).getBody();
    }

    private static Map<String, Object> findParameterConfiguration(String clusterName) throws UnExpectedRequestException, IOException {
        ClusterInfo clusterInfo = SpringContextHolder.getBean(ClusterInfoDao.class).findByClusterName(clusterName);
        if(clusterInfo ==null){
            throw new UnExpectedRequestException("fail to find clusterInfo configuration");
        }
        if(StringUtils.isBlank(clusterInfo.getWtssJson())){
            throw new UnExpectedRequestException("WtssJson configuration is null");
        }

        return new ObjectMapper().readValue(clusterInfo.getWtssJson(),Map.class);
    }


    private static String getScheduledResult(String operationUser, String realUser, String clusterName) throws UnExpectedRequestException, IOException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException, URISyntaxException {
        Map<String, Object> map = findParameterConfiguration(clusterName);

        String token = encrypt(map.get("loginAccount").toString(), map.get("publickey").toString());
        //username放运维用户，normalUserName放实名用户
        String url = String.format("%s/checkin?action=login&username=%s&userpwd=&normalUserName=%s&common_secret=%s",map.get("path").toString(), operationUser, realUser, token);
        URI uri = new URI(url);
        logger.info("发起请求 url: " + url);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(headers), String.class).getBody();
    }

    public static String getSessionId(String operationUser, String realUser, String cluster) throws UnExpectedRequestException {
        String response;
        try {
            response = getScheduledResult(operationUser, realUser, cluster);
        } catch (Exception e) {
            logger.error(">>>>>>>>>> Failed To get sessionId from Scheduled System  <<<<<<<<<<", e.getMessage());
            throw new UnExpectedRequestException("Error! Failed To get sessionId from Scheduled System " + e.getMessage());
        }
        Gson gson = new Gson();
        Map<String, Object> resultMap = gson.fromJson(response, Map.class);

        if (resultMap.containsKey(ThirdPartyConstants.WTSS_RES_SESSION_ID)) {
            return resultMap.get(ThirdPartyConstants.WTSS_RES_SESSION_ID).toString();
        } else {
            if (resultMap.containsKey(ThirdPartyConstants.WTSS_RES_ERROR)) {
                throw new UnExpectedRequestException("Error!Failed To get sessionId from Scheduled System：" + resultMap.get(ThirdPartyConstants.WTSS_RES_ERROR).toString());
            } else {
                throw new UnExpectedRequestException("Error!Failed To get sessionId from Scheduled System");
            }
        }

    }

    public static String getSessionIdByRealUser(String realUser, String cluster) throws UnExpectedRequestException {
        String response = null;
        try {
            response = getScheduledResultByRealUser(realUser, cluster);
        } catch (Exception e) {
            logger.error(">>>>>>>>>> Failed To get sessionId from Scheduled System  <<<<<<<<<<", e.getMessage());
            throw new UnExpectedRequestException("Error! Failed To get sessionId from Scheduled System " + e.getMessage());
        }
        Gson gson = new Gson();
        Map<String, Object> resultMap = gson.fromJson(response, Map.class);

        if (resultMap.containsKey(ThirdPartyConstants.WTSS_RES_SESSION_ID)) {
            return resultMap.get(ThirdPartyConstants.WTSS_RES_SESSION_ID).toString();
        } else {
            if (resultMap.containsKey(ThirdPartyConstants.WTSS_RES_ERROR)) {
                throw new UnExpectedRequestException("Error!Failed To get sessionId from Scheduled System：" + resultMap.get(ThirdPartyConstants.WTSS_RES_ERROR).toString());
            } else {
                throw new UnExpectedRequestException("Error!Failed To get sessionId from Scheduled System");
            }
        }

    }


}
