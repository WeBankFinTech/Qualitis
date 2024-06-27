package com.webank.wedatasphere.qualitis.util;

import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author v_minminghe@webank.com
 * @date 2024-06-03 14:27
 * @description
 */
public class SignUtil {

    public static String generateSignature(String appId, String appToken, String nonce, String timeStamp) {
        MessageDigest hash;
        StringBuilder resultInner = new StringBuilder();
        StringBuilder resultOuter = new StringBuilder();
        String plain = appId + nonce + timeStamp;
        String signature = null;
        try {
            hash = MessageDigest.getInstance("SHA-256");
            hash.update(plain.getBytes("UTF-8"));
            resultInner.append(new BigInteger(1, hash.digest()).toString(16));
            String inner = StringUtils.leftPad(resultInner.toString(), 32, '0');

            hash.reset();
            hash.update(inner.concat(appToken).getBytes("UTF-8"));
            resultOuter.append(new BigInteger(1, hash.digest()).toString(16));
            signature = StringUtils.leftPad(resultOuter.toString(), 32, '0');

        } catch (NoSuchAlgorithmException e) {
//            doing nothing

        } catch (UnsupportedEncodingException e) {
//            doing nothing
        }
        return signature;
    }
}
