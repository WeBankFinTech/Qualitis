package com.webank.wedatasphere.qualitis.request.user;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2024-03-26 17:05
 * @description
 */
public class ItsmRequest implements Serializable {

    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    private static class ItsmDataListDto<T> {

        private List<T> dataList;

        public List<T> getDataList() {
            return dataList;
        }

        public void setDataList(List<T> dataList) {
            this.dataList = dataList;
        }
    }

    public List<ItsmUserRequest> getItsmUserList() {
        if (StringUtils.isBlank(this.data)) {
            return Collections.emptyList();
        }
        TypeReference<ItsmDataListDto<ItsmUserRequest>> typeRef = new TypeReference<ItsmDataListDto<ItsmUserRequest>>(){};
        try {
            ItsmDataListDto<ItsmUserRequest> dataListObject = new ObjectMapper().readValue(this.data, typeRef);
            return dataListObject.getDataList();
        } catch (IOException e) {
            // doNothing
        }
        return Collections.emptyList();
    }

    public List<Map<String, Object>> getItsmRoleUserList() {
        if (StringUtils.isBlank(this.data)) {
            return Collections.emptyList();
        }
        TypeReference<ItsmDataListDto<Map<String, Object>>> typeRef = new TypeReference<ItsmDataListDto<Map<String, Object>>>(){};
        try {
            ItsmDataListDto<Map<String, Object>> dataListObject = new ObjectMapper().readValue(this.data, typeRef);
            return dataListObject.getDataList();
        } catch (IOException e) {
            // doNothing
        }
        return Collections.emptyList();
    }

    public List<ItsmAlertWhitelistRequest> getItsmAlertWhitelist() {
        if (StringUtils.isBlank(this.data)) {
            return Collections.emptyList();
        }
        TypeReference<ItsmDataListDto<ItsmAlertWhitelistRequest>> typeRef = new TypeReference<ItsmDataListDto<ItsmAlertWhitelistRequest>>(){};
        try {
            ItsmDataListDto<ItsmAlertWhitelistRequest> dataListObject = new ObjectMapper().readValue(this.data, typeRef);
            return dataListObject.getDataList();
        } catch (IOException e) {
            // doNothing
        }
        return Collections.emptyList();
    }

}
