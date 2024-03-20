package com.webank.wedatasphere.qualitis.metadata.request;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2023-05-15 9:31
 * @description
 */
public class ModifyDataSourceParameterRequest {

    private Long linkisDataSourceId;
    private String comment;
    private Map<String, Object> connectParams;

    public Long getLinkisDataSourceId() {
        return linkisDataSourceId;
    }

    public void setLinkisDataSourceId(Long linkisDataSourceId) {
        this.linkisDataSourceId = linkisDataSourceId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Map<String, Object> getConnectParams() {
        return connectParams;
    }

    public void setConnectParams(Map<String, Object> connectParams) {
        this.connectParams = connectParams;
    }

    public void setEnvIdArray(List<String> envIdArray) throws UnExpectedRequestException {
        if (null == connectParams) {
            throw new UnExpectedRequestException("Field {connectParams} is null");
        }
        connectParams.put("envIdArray", envIdArray);
    }
}
