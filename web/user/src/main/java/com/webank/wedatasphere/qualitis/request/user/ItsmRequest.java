package com.webank.wedatasphere.qualitis.request.user;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

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
