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

package com.webank.wedatasphere.qualitis.password;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author howeye
 */
public class RandomPasswordGenerator {

    protected static final char[] CHAR_DIC = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '~', '!', '@', '#', '$', '%', '^', '-', '+'
    };

    public static String generate(int length) {
        int i = 0;
        int count = 0;

        StringBuffer stringBuffer = new StringBuffer("");
        List<Character> list = new ArrayList<>();
        SecureRandom secureRandom = new SecureRandom();

        for (i = 0; i < CHAR_DIC.length; i++) {
            list.add(CHAR_DIC[i]);
        }
        Collections.shuffle(list);
        while(count < length){
            i = secureRandom.nextInt(list.size());
            stringBuffer.append(list.get(i));
            count++;
        }
        return stringBuffer.toString();
    }

}
