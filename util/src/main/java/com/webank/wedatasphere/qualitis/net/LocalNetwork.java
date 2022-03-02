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

package com.webank.wedatasphere.qualitis.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author howeye
 */
public class LocalNetwork {

    private static final String WINDOWS_PREFIX = "win";
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalNetwork.class);

    private LocalNetwork() {
        // Default Contructor
    }

    public static String getLocalIpByFirstNetCard() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith(WINDOWS_PREFIX)) {
            try {
                for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {
                    NetworkInterface item = e.nextElement();
                    for (InterfaceAddress address : item.getInterfaceAddresses()) {
                        if (item.isLoopback() || !item.isUp()) {
                            continue;
                        }
                        if (address.getAddress() instanceof Inet4Address) {
                            Inet4Address inet4Address = (Inet4Address) address.getAddress();
                            return inet4Address.getHostAddress();
                        }
                    }
                }
                return InetAddress.getLocalHost().getHostAddress();
            } catch (SocketException | UnknownHostException e) {
                throw new RuntimeException(e);
            }
        } else {
            Process process = null;
            List<String> processList = new ArrayList<String>();
            try {
                process = Runtime.getRuntime().exec("hostname -i");
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = "";
                while ((line = input.readLine()) != null) {
                    processList.add(line);
                }
                input.close();
            } catch (IOException e) {
                LOGGER.error("LocalNetwor:getLocalIpByFirstNetCard exception.", e.getMessage());
            }

            if (processList.size() == 1) {
                return processList.get(0);
            }
            return null;
        }
    }

}
