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

import org.apache.commons.lang.time.FastDateFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author v_wblwyan
 * @date 2018-11-1
 */
public class DateUtils {

  private static final FastDateFormat PRINT_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

  private DateUtils() {
    // Default Constructor
  }

  public static int getDayDiffBetween(Date startDate, Date endDate) {
    long diff = endDate.getTime() - startDate.getTime();
    float days = (float) diff/(1000 * 60 * 60 * 24);
    return BigDecimal.valueOf(days).setScale(1, BigDecimal.ROUND_HALF_UP).intValue();
  }

  public static String now() {
    return PRINT_TIME_FORMAT.format(new Date());
  }

  public static String now(String format) {
    FastDateFormat printTimeFormat = FastDateFormat.getInstance(format);
    return printTimeFormat.format(new Date());
  }

}
