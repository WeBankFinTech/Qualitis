package com.webank.wedatasphere.qualitis.util;

import com.webank.wedatasphere.qualitis.exception.HttpMethodException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author allenzhou@webank.com
 * @date 2022/6/22 16:30
 */
public class HttpDmUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpDmUtils.class);

    private static String zhPattern = "[\u4e00-\u9fa5]+";

    public static String httpFetch(HttpMethod method, String urlStr,String loginUserId, String env, String signature, String json)
        throws IOException {
        String encodeUrl = encodeUrlComponent(urlStr, "UTF-8");
        URLConnection conn;
        InputStream in = null;
        DataOutputStream out = null;
        String filteredUrl = validateUrl(encodeUrl);
        try {
            URL url = new URL(filteredUrl);
            conn = url.openConnection();

            HttpURLConnection httpUrlConnection = (HttpURLConnection) conn;
            //禁止30x跳转
            httpUrlConnection.setInstanceFollowRedirects(false);

            if (method == HttpMethod.POST) {
                httpUrlConnection.setRequestProperty("Content-type", "application/json;charset=UTF-8");
            }
            httpUrlConnection.setRequestMethod(method.toString());
            httpUrlConnection.setRequestProperty("Accept-Charset", "UTF-8");
            httpUrlConnection.setRequestProperty("LOGIN_USER_ID", loginUserId);
            httpUrlConnection.setRequestProperty("tgc_sf" ,signature);
            httpUrlConnection.setRequestProperty("envFlag", env);
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.connect();
            if (method == HttpMethod.POST && json != null) {
                out = new DataOutputStream(httpUrlConnection.getOutputStream());
                out.write(json.getBytes("UTF-8"));
                out.close();
            }

            in = httpUrlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
                result.append('\n');
            }
            in.close();
            ((HttpURLConnection) conn).disconnect();
            return result.toString();
        } finally {
            try {
                IOUtils.close(out);
            } finally {
                IOUtils.close(in);
            }
        }
    }

    private static String encodeUrlComponent(String str, String charset) {
        Pattern p = Pattern.compile(zhPattern);
        Matcher m = p.matcher(str);
        StringBuffer b = new StringBuffer();
        while (m.find()) {
            try {
                m.appendReplacement(b, URLEncoder.encode(m.group(0), charset));
            } catch (UnsupportedEncodingException e) {
                throw new HttpMethodException("Fail to encode url");
            }
        }
        m.appendTail(b);
        return b.toString();
    }

    /**
     * SSRF漏洞防御
     * 1.限制协议为HTTP/HTTPS协议
     * 2.禁止30x跳转（http重定向状态码）
     * 3.设置URL白名单或者限制内网IP
     * 4.限制请求的端口为http常用的端口
     *
     * 防止XSS攻击
     * 1.输入数据验证
     * 2.输出数据编码
     * 3.过滤特殊字符
     * 4.使用安全框架
     * @param url
     */
    private static String validateUrl(String url) {
        if (url != null && !url.startsWith("http")) {
            LOGGER.error("url must start with http.");
        }

        // 过滤特殊字符
        String filteredData = url.replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\\(", "&#40;")
                .replaceAll("\\)", "&#41;")
                .replaceAll("'", "&#39;")
                .replaceAll("eval\\((.*)\\)", "")
                .replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        return filteredData;
    }
}
