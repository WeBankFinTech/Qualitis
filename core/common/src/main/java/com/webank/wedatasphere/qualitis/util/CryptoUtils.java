package com.webank.wedatasphere.qualitis.util;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

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
            ByteArrayInputStream bis =
                    new ByteArrayInputStream(new Base64().decode(str.getBytes("UTF-8")));
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object o = ois.readObject();
            bis.close();
            ois.close();
            return o;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
