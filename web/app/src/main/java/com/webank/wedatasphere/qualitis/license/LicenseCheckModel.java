package com.webank.wedatasphere.qualitis.license;

import java.io.Serializable;
import java.util.List;

/**
 * 自定义需要校验的License参数
 */
public class LicenseCheckModel implements Serializable{

    private static final long serialVersionUID = 8600137500316662317L;
    /**
     * 可被允许的IP地址
     */
    private List<String> ipAddress;

    /**
     * 可被允许的MAC地址
     */
    private List<String> macAddress;


    public List<String> getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(List<String> ipAddress) {
        this.ipAddress = ipAddress;
    }

    public List<String> getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(List<String> macAddress) {
        this.macAddress = macAddress;
    }


    @Override
    public String toString() {
        return "com.webank.license.LicenseCheckModel{" +
                "ipAddress=" + ipAddress +
                ", macAddress=" + macAddress +
                '}';
    }
}
