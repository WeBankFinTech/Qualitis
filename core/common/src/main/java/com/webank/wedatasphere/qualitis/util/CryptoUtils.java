package com.webank.wedatasphere.qualitis.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.serialization.ValidatingObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * @author v_minminghe@webank.com
 * @date 2022-08-11 17:49
 * @description
 */
public class CryptoUtils {

    public static String encode(Serializable o) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            oos.flush();
            oos.close();
            bos.close();
            return new String(new Base64().encode(bos.toByteArray()));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static Object decode(String str) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(new Base64().decode(str.getBytes(StandardCharsets.UTF_8)));
            ValidatingObjectInputStream vois = new ValidatingObjectInputStream(bis);
            vois.accept(String.class);
            Object o = vois.readObject();
            bis.close();
            vois.close();
            return o;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
