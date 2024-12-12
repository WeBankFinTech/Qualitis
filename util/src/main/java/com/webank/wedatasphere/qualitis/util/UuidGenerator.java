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

package com.webank.wedatasphere.qualitis.util;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * @author howeye
 */
public class UuidGenerator {
    private static SecureRandom secureRandom;
    static {
        secureRandom = new SecureRandom();
    }

    private UuidGenerator() {
        secureRandom = new SecureRandom();
    }

    public static String generate() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    public static String generateRandom(int count) {
        StringBuffer randomNumberSeq = new StringBuffer();
        for (int i = 0; i < count; i ++) {
            randomNumberSeq.append(secureRandom.nextInt(10) + "");
        }
        return randomNumberSeq.toString();
    }
}
