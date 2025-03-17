package com.webank.wedatasphere.qualitis.client.impl;

import com.webank.wedatasphere.qualitis.client.MailClient;
import com.webank.wedatasphere.qualitis.config.EsbSdkConfig;
import com.webank.wedatasphere.qualitis.config.OperateCiConfig;
import com.webank.wedatasphere.qualitis.constant.MailConstants;
import com.webank.wedatasphere.qualitis.dao.EsbMailRecordDao;
import com.webank.wedatasphere.qualitis.entity.EsbMailRecord;
import org.junit.platform.commons.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @author v_gaojiedeng@webank.com
 */
@Component
public class EsbMailClient implements MailClient {

    @Autowired
    private EsbSdkConfig esbSdkConfig;
    @Autowired
    private OperateCiConfig operateCiConfig;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private EsbMailRecordDao esbMailRecordDao;

    @Value("${overseas_external_version.enable:false}")
    private Boolean overseasVersionEnabled;

    private static final Logger LOGGER = LoggerFactory.getLogger(EsbMailClient.class);
    private static final Integer FOUR = 4;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void sendEsbMail(String result, String createUser) throws Exception {
        if (overseasVersionEnabled){
            LOGGER.info("skip send esb mail.");
            return;
        }
        if (StringUtils.isBlank(result)) {
            throw new Exception("Email Send Failure json data is empty: " + result);
        }

        String httpProfile = operateCiConfig.getEfHost();
        String appId = operateCiConfig.getEfAppId();
        String appToken = operateCiConfig.getEfAppToken();

        String url = constructUrlWithSignature(httpProfile, appId, appToken, null);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        HttpEntity<Object> entity = new HttpEntity<>(result, headers);

        LOGGER.info("Start to send Esb mail. url: {}, method: {}, body: {}", url, HttpMethod.POST.name(), entity);
        Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);

        Integer code = Integer.valueOf(response.get("Code").toString());
        String message = String.valueOf(response.get("Message").toString());
        //code != 0，抛出异常
        if (code != 0) {
            throw new Exception("Email Send Failure: " + message + "; Return Code: " + code);
        }
        LOGGER.info("Succeed to send Esb mail. response: {}", response);

        //保存请求esb邮件记录
        EsbMailRecord esbMailRecord = new EsbMailRecord();
        esbMailRecord.setUrl(url);
        esbMailRecord.setCode(String.valueOf(code));
        esbMailRecord.setResponseMessage(message);
        esbMailRecord.setAssemblyResult(result);
        esbMailRecord.setCreateTime(simpleDateFormat.format(new Date()));
        esbMailRecord.setCreateUser(createUser);
//        esbMailRecordDao.save(esbMailRecord);
        LOGGER.info(">>>>>>>>>> Esb Mail Record Object :<<<<<<<<<< : " + esbMailRecord.toString());
    }

    @Override
    public String getHrMessage() throws Exception {
        if (overseasVersionEnabled){
            LOGGER.info("get hr message return Code:0 Data:[]");
            return "{\"Code\": 0,\"Data\": []}";
        }
        String httpProfile = operateCiConfig.getEfHost();
        String appId = operateCiConfig.getEfAppId();
        String appToken = operateCiConfig.getEfAppToken();
        String url = constructUrlWithSignature(httpProfile, appId, appToken, "hr");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity entity = new HttpEntity<>(headers);

        LOGGER.info("Start to get Hr message. url: {}, method: {}, body: {}", url, HttpMethod.GET, entity);
        String response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
        LOGGER.info("Succeed to get Hr message. repsonse: {}", response);
        //需要先base64解码，再使用gzip解压缩
        String result = decodeBase64AndGZip(response);
        if (StringUtils.isBlank(result)) {
            throw new Exception("Getting Hr message Failure: " + result);
        }
        return result;
    }

    /**
     * @param
     * @return 签名后的请求url
     * @Description: 签名函数，所有client共用
     */
    public String constructUrlWithSignature(String httpProfile, String appId, String appToken, String hr) {
        String nonce = getRondom(5);
        String timestamp = "" + Calendar.getInstance().getTimeInMillis() / 1000L;
        String signature = sign(sign(appId + nonce + timestamp, "utf-8") + appToken, "utf-8");
        String url, urlSignature;
        if (StringUtils.isNotBlank(hr)) {
            url = httpProfile + esbSdkConfig.getHrQuerySuffixPath();
            urlSignature = MessageFormat.format(url, appId, nonce, timestamp, signature, 1, true);
        } else {
            url = httpProfile + esbSdkConfig.getEmailSendSuffixPath();
            urlSignature = MessageFormat.format(url, appId, nonce, timestamp, signature);
        }
        urlSignature = addTraceInfo(urlSignature);
        return urlSignature;
    }

    public static String sign(String text, String input_charset) {
        return DigestUtils.md5DigestAsHex(getContentBytes(text, input_charset));
    }

    private static byte[] getContentBytes(String content, String charset) {
        if (charset != null && charset.isEmpty()) {
            try {
                return content.getBytes(charset);
            } catch (UnsupportedEncodingException var3) {
                throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
            }
        } else {
            return content.getBytes();
        }
    }

    public static String getRondom(int len) {
        String randomStr = "";
        SecureRandom r = new SecureRandom();

        for (int i = 0; i < len; ++i) {
            randomStr = randomStr + r.nextInt(len - 1);
        }

        return randomStr;
    }

    public String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception var2) {
            LOGGER.warn("can not get local unitAddress {}", var2);
            return "";
        }
    }

    private String addTraceInfo(String url) {
        StringBuilder builder = new StringBuilder();
        builder.append(url).append("&").append(MailConstants.ESB_SDK_REQID).append("=")
                .append(generateFlowID(getLocalIp())).toString();
        return builder.toString();
    }

    private static int count = 0;

    public static String generateFlowID(String hostIP) {
        if (hostIP == null || "".equals(hostIP)) {
            return null;
        }
        hostIP = getHostIP(hostIP);
        if (hostIP == null) {
            return null;
        }
        //时间戳
        String timeStamp = getTimeStamp();
        //4位数，依次递增
        String count = getCountValue();
        String countValue = handleCountValue(count);
        //版本号
        String version = getVersion();
        return new StringBuffer().append(hostIP).append(timeStamp).append(countValue).append(version).toString();
    }

    private static String getHostIP(String hostIP) {
        String[] split = hostIP.split("\\.");
        StringBuilder bf = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            if (split[i].length() < 3) {
                int size = split[i].length();
                //填0补充
                if (size == 1) {
                    split[i] = new StringBuffer().append("00").append(split[i]).toString();
                } else if (size == 2) {
                    split[i] = new StringBuffer().append("0").append(split[i]).toString();
                }
            }
            //格式错误
            else if (split[i].length() > 3) {
                return null;
            }
            bf.append(split[i]);
        }
        return bf.toString();
    }

    private static String getTimeStamp() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String timeStamp = format.format(date);
        return timeStamp.substring(2);
    }

    private static synchronized String getCountValue() {
        //先获取值，判断是否已达到 9999
        if (count == MailConstants.MAX_COUNT) {
            count = 0;
        } else {
            count++;
        }
        return String.valueOf(count);
    }

    private static String handleCountValue(String value) {
        StringBuilder bf = new StringBuilder();
        char[] chars = value.toCharArray();
        if (chars.length == FOUR) {
            return value;
        }
        for (int i = 0; i < FOUR - chars.length; i++) {
            bf.append('0');
        }
        bf.append(value);
        return bf.toString();
    }

    private static String getVersion() {
        String version = MailConstants.VERSION;
        return version.replaceAll("\\.", "");
    }

    public static String decodeBase64AndGZip(String input) throws IOException {
        byte[] bytes;
        String out = input;

        try (ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getDecoder().decode(input));
             GZIPInputStream gzip = new GZIPInputStream(bis);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            int num;
            while ((num = gzip.read(buf, 0, buf.length)) != -1) {
                bos.write(buf, 0, num);
            }

            bytes = bos.toByteArray();
            out = new String(bytes, StandardCharsets.UTF_8);
            bos.flush();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return out;
    }

}
