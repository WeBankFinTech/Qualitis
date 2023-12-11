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

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author allenzhou
 * @date 2022-04-23
 */
public class BashUtils {

  private BashUtils() {
    // Default Constructor
  }

  /**
   * Intercept value.
   * @param key
   * @param templateFunction
   * @return
   */
  public static String getCommonValue(String key, String templateFunction) {
    if (templateFunction.indexOf(key) < 0) {
      return "";
    }

    int space = ' ';

    int start = templateFunction.indexOf(key) + key.length();
    int end = -1;

    boolean scanToCharacter = false;
    for (int i = start; i < templateFunction.length(); i ++) {
      if (scanToCharacter && space == templateFunction.charAt(i)) {
        end = i;
        break;
      }

      if (! scanToCharacter && space != (templateFunction.charAt(i))) {
        scanToCharacter = true;
      }
    }

    if (end < start) {
      return "";
    }
    return templateFunction.substring(start, end).trim();
  }
}
