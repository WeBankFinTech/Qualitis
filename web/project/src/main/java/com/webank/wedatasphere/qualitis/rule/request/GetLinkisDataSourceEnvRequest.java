package com.webank.wedatasphere.qualitis.rule.request;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2023-12-13 10:05
 * @description
 */
public class GetLinkisDataSourceEnvRequest {

    private Long linkisDataSourceId;
    private List<Long> envIdList;
    private List<String> dcnNums;
    private List<String> logicAreas;

    public Long getLinkisDataSourceId() {
        return linkisDataSourceId;
    }

    public void setLinkisDataSourceId(Long linkisDataSourceId) {
        this.linkisDataSourceId = linkisDataSourceId;
    }

    public List<Long> getEnvIdList() {
        return envIdList;
    }

    public void setEnvIdList(List<Long> envIdList) {
        this.envIdList = envIdList;
    }

    public List<String> getDcnNums() {
        return dcnNums;
    }

    public void setDcnNums(List<String> dcnNums) {
        this.dcnNums = dcnNums;
    }

    public List<String> getLogicAreas() {
        return logicAreas;
    }

    public void setLogicAreas(List<String> logicAreas) {
        this.logicAreas = logicAreas;
    }
}
