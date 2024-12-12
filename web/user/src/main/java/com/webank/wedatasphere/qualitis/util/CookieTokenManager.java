package com.webank.wedatasphere.qualitis.util;

import com.google.gson.Gson;
import com.webank.wedatasphere.qualitis.model.DmTicketValue;
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.util.CookieGenerator;

import java.io.UnsupportedEncodingException;

/**
 * @author allenzhou@webank.com
 * @date 2022/6/22 16:20
 */
public class CookieTokenManager {
    public static final long SESSION_MAX_IDLE_MILLIS  = 8L * 60L * 60L * 1000L ;
    public static final String COOKIE_UTF_8 = "utf-8";
    public static final String COOKIE_PATH = "/";
    private CookieGenerator cookieGenerator;
    private final String cookieName;
    private String randomHashSalt;

    private static final Gson GSON = new Gson();

    public CookieTokenManager(String cookieDomain, String cookieName, String randomHashSalt) {
        cookieGenerator = new CookieGenerator();
        cookieGenerator.setCookieHttpOnly(true);
        cookieGenerator.setCookieName(cookieName);
        cookieGenerator.setCookiePath(COOKIE_PATH);
        cookieGenerator.setCookieDomain(cookieDomain);
        cookieGenerator.setCookieMaxAge((int) (SESSION_MAX_IDLE_MILLIS / 1000));

        this.cookieName = cookieName;
        this.randomHashSalt = randomHashSalt;
    }

    private String sha(DmTicketValue dmTicketValue) throws UnsupportedEncodingException {
        return SHAEncrypt.Encrypt(String.format("%s%s%s%d",
            dmTicketValue.getLoginName(),
            randomHashSalt,
            dmTicketValue.getLoginFullName(),
            dmTicketValue.getExpires()),null);
    }

    public DmTicketValue buildDmTicketValue(String userName, String fullUserName){
        long now = System.currentTimeMillis();
        long expires = now;
        DmTicketValue dmTicketValue=new DmTicketValue();
        dmTicketValue.setExpires(expires);
        dmTicketValue.setLoginName(userName);
        dmTicketValue.setHash(randomHashSalt);
        dmTicketValue.setLoginFullName(fullUserName);
        return dmTicketValue;
    }

    public String calcCookieValue(DmTicketValue dmTicketValue, long expires) throws UnsupportedEncodingException {
        dmTicketValue.setExpires(expires);
        dmTicketValue.setHash(sha(dmTicketValue));
        String json = GSON.toJson(dmTicketValue);
        return encodeCookieValue(json);
    }

    private String encodeCookieValue(String json) throws UnsupportedEncodingException {
        String encode = Base64.encodeBase64String(json.getBytes(COOKIE_UTF_8));
        return encode;
    }
}
