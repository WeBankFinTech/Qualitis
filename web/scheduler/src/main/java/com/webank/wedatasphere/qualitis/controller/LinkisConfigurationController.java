package com.webank.wedatasphere.qualitis.controller;

/**
 * @author allenzhou@webank.com
 * @date 2021/5/7 14:35
 */

import com.webank.wedatasphere.qualitis.client.LinkisConfiguration;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.dao.AuthListDao;
import com.webank.wedatasphere.qualitis.entity.AuthList;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.SaveFullTreeRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Path("/api/v1/projector/configuration")
public class LinkisConfigurationController {
    @Autowired
    private LinkisConfiguration linkisConfiguration;
    @Autowired
    private LinkisConfig linkisConfig;

    private static MessageDigest hash;

    static {
        try {
            hash = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkisConfigurationController.class);

    private HttpServletRequest httpServletRequest;

    @Autowired
    private AuthListDao authListDao;

    public LinkisConfigurationController(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getFullTree(@QueryParam("cluster_name") String clusterName ,@QueryParam("user_name") String userName) throws UnExpectedRequestException {
        if (StringUtils.isBlank(userName)) {
            userName =  HttpUtils.getUserName(httpServletRequest);
        }
        try {
            Map<String, Map<String, Object>> response = linkisConfiguration.getFullTree(clusterName, userName);

            if (hash != null) {
                Map<String, Object> map = new HashMap<>(2);

                String nonce = "16895";
                String appId = "linkis_id";
                AuthList authList = authListDao.findByAppId(appId);
                if (null == authList) {
                    throw new UnExpectedRequestException("No Authentication!");
                }

                String timestamp = String.valueOf(System.currentTimeMillis());

                String plain = appId + nonce + timestamp;

                StringBuilder resultInner = new StringBuilder();
                StringBuilder resultOuter = new StringBuilder();

                try {
                    hash.update(plain.getBytes("UTF-8"));
                    resultInner.append(new BigInteger(1, hash.digest()).toString(16));
                    String inner = StringUtils.leftPad(resultInner.toString(), 32, '0');

                    hash.reset();

                    hash.update(inner.concat(authList.getAppToken()).getBytes("UTF-8"));
                    resultOuter.append(new BigInteger(1, hash.digest()).toString(16));
                    String outer = StringUtils.leftPad(resultOuter.toString(), 32, '0');

                    map.put("timestamp", timestamp);
                    map.put("signature", outer);

                } catch (UnsupportedEncodingException e) {
                    LOGGER.info(e.getMessage(), e);
                }

                response.put("OuterExecutionCheckParam", map);
            }

            return new GeneralResponse<>("200", "{&SUCCESS_TO_GET_STARTUP_PATAM}", response);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to , caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_STARTUP_PATAM}", null);
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map<String, Object>> saveFullTree(SaveFullTreeRequest request) throws UnExpectedRequestException {
        String userName = request.getUserName();
        if (StringUtils.isBlank(userName)) {
            userName = HttpUtils.getUserName(httpServletRequest);
        }
        try {
            Map<String, Object> response =  linkisConfiguration.saveFullTree(request.getClusterName(), linkisConfig.getAppName()
                , request.getFullTree(), userName);
            return new GeneralResponse<>("200", "{&SUCCESS_TO_MODIFY_STARTUP_PATAM}", response);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to , caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_MODIFY_STARTUP_PATAM}", null);
        }
    }
}
