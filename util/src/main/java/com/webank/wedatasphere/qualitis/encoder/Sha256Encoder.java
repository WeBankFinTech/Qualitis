/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.encoder;

import com.google.common.base.Charsets;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import java.math.BigInteger;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author howeye
 */
public class Sha256Encoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(Sha256Encoder.class);

    private Sha256Encoder() {
        // Default Constructor
    }
    private static final char[] DIGITS_LOWER =
        { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f' };
    private static final char[] DIGITS_UPPER =
        { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
            'E', 'F' };

    public static String encode(String str, boolean toLowerCase, boolean length32) {
        byte[] bytes;
        Hasher hasher = Hashing.sha256().newHasher();
        bytes = hasher.putString(str, Charsets.UTF_8).hash().asBytes();
        return length32 ? bytes2Hex(bytes, toLowerCase) : bytes2Hex(bytes, toLowerCase).substring(8,24);
    }

    /**
     * bytes list to String
     *
     * @param data
     * @param toLowerCase
     * @return
     */
    public static String bytes2Hex(final byte[] data, final boolean toLowerCase) {
        return bytes2Hex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    /**
     * bytes list to String
     *
     * @param data
     * @param toDigits
     * @return
     */
    private static String bytes2Hex(final byte[] data, final char[] toDigits) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return new String(out);
    }

    public static String encode(String str) {
        MessageDigest hash;
        StringBuffer result = new StringBuffer();
        try {
            hash = MessageDigest.getInstance("SHA-256");
            hash.update(str.getBytes("UTF-8"));
            result.append(new BigInteger(1, hash.digest()).toString(16));

            return StringUtils.leftPad(result.toString(), 32, '0');
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return result.toString();
    }

}
