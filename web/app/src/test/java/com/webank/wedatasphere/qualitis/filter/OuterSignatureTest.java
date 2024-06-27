package com.webank.wedatasphere.qualitis.filter;
import cn.hutool.crypto.digest.DigestUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author v_minminghe@webank.com
 * @date 2024-04-10 9:34
 * @description
 */
@RunWith(SpringRunner.class)
public class OuterSignatureTest {

    @Test
    public void generateSign() {
        String nonce = "16895";
        String timeStamp = String.valueOf(System.currentTimeMillis());
        MessageDigest hash;

        StringBuilder resultInner = new StringBuilder();
        StringBuilder resultOuter = new StringBuilder();
        //app_id:  linkis_id
        String plain = "linkis_id" + nonce + timeStamp;

        try {
            hash = MessageDigest.getInstance("SHA-256");
            hash.update(plain.getBytes("UTF-8"));
            resultInner.append(new BigInteger(1, hash.digest()).toString(16));
            String inner = StringUtils.leftPad(resultInner.toString(), 32, '0');

            hash.reset();
            //app_token :***REMOVED***
            hash.update(inner.concat("***REMOVED***").getBytes("UTF-8"));
            resultOuter.append(new BigInteger(1, hash.digest()).toString(16));
            String signature = StringUtils.leftPad(resultOuter.toString(), 32, '0');
            Assert.hasText(signature, "empty string");

            //加密签名方式1
            //System.out.println(getSignature(nonce, timeStamp, "***REMOVED***", "linkis_id"));
            //加密签名方式2
//            System.out.println("timeStamp: " + timeStamp);
//            System.out.println("signature: " + signature);

        } catch (NoSuchAlgorithmException e) {
//            doing nothing
        } catch (UnsupportedEncodingException e) {
//            doing nothing
        }
    }

    @Test
    public void generateItsmSign() {
//        uat
        String secretKey = "4b968eca0ab8302d9bcd711e7177769c880ad28588d75db101657a7b665d2807";
        long timeStamp = System.currentTimeMillis();
        String signature = DigestUtil.sha256Hex(secretKey + timeStamp);
        Assert.hasText(signature, "empty string");
//        System.out.println("timeStamp: " + timeStamp);
//        System.out.println("signature: " + signature);
    }

}
