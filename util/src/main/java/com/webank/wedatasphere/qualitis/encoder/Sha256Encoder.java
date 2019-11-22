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

    public static String encode(String str) {
        MessageDigest md;
        StringBuffer result = new StringBuffer();
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(str.getBytes("UTF-8"));
            for (byte b : md.digest()) {
                result.append(String.format("%02x", b));
            }
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return result.toString();
    }

}
