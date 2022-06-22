/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appconn.qualitis.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

/**
 * @author allenzhou@webank.com
 * @date 2021/7/12 10:57
 */
public class HttpUtils {
    public static URI buildUrI(String baseUrl, String path, String appId, String appToken,
        String nonce, String timestamp) throws NoSuchAlgorithmException, URISyntaxException {
        String signature = getSignature(appId, appToken, nonce, timestamp);
        StringBuffer uriBuffer = new StringBuffer(baseUrl);
        uriBuffer.append(path).append("?")
            .append("app_id=").append(appId).append("&")
            .append("nonce=").append(nonce).append("&")
            .append("timestamp=").append(timestamp).append("&")
            .append("signature=").append(signature);

        URI uri = new URI(uriBuffer.toString());
        return uri;
    }

    public static String getSignature(String appId, String appToken, String nonce, String timestamp) throws NoSuchAlgorithmException {
        return Sha256Utils.getSHA256L32(Sha256Utils.getSHA256L32(appId + nonce + timestamp) + appToken);
    }
}

