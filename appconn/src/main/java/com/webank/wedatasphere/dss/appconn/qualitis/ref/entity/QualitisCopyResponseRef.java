package com.webank.wedatasphere.dss.appconn.qualitis.ref.entity;

import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;
import java.util.Map;

/**
 * @author allenzhou
 */
public class QualitisCopyResponseRef extends AbstractResponseRef {


    private Map<String, Object> newJobContent;

    public QualitisCopyResponseRef(Map<String, Object> jobContent, String responseBody, int status) throws Exception {
        super(responseBody, status);
        this.newJobContent = jobContent;
    }

    @Override
    public Map<String, Object> toMap() {
        return newJobContent;
    }

    @Override
    public String getErrorMsg() {
        return null;
    }

    @Override
    public boolean isFailed() {
        return false;
    }

}
