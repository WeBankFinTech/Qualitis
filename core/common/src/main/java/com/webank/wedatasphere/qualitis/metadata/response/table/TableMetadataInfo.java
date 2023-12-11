package com.webank.wedatasphere.qualitis.metadata.response.table;

import java.util.List;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2022-06-01 11:36
 * @description
 */
public class TableMetadataInfo {

    private String searchType;
    private String source;
    private String rawName;
    private String urn;
    private List<SubSystem> subSystemSet;
    private List<Map<String, String>> pathList;
    private String busDept;
    private String devDept;

    public static TableMetadataInfo build(){
        return new TableMetadataInfo();
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRawName() {
        return rawName;
    }

    public void setRawName(String rawName) {
        this.rawName = rawName;
    }

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public List<SubSystem> getSubSystemSet() {
        return subSystemSet;
    }

    public void setSubSystemSet(List<SubSystem> subSystemSet) {
        this.subSystemSet = subSystemSet;
    }

    public String getBusDept() {
        return busDept;
    }

    public void setBusDept(String busDept) {
        this.busDept = busDept;
    }

    public String getDevDept() {
        return devDept;
    }

    public void setDevDept(String devDept) {
        this.devDept = devDept;
    }

    public List<Map<String, String>> getPathList() {
        return pathList;
    }

    public void setPathList(List<Map<String, String>> pathList) {
        this.pathList = pathList;
    }

    @Override
    public String toString() {
        return "TableDeptInfo{" +
                "searchType='" + searchType + '\'' +
                ", source='" + source + '\'' +
                ", rawName='" + rawName + '\'' +
                ", urn='" + urn + '\'' +
                ", subSystemSet=" + subSystemSet +
                ", busDept='" + busDept + '\'' +
                ", devDept='" + devDept + '\'' +
                '}';
    }

    public static class SubSystem {

        private String fid;
        private String name;

        public String getFid() {
            return fid;
        }

        public void setFid(String fid) {
            this.fid = fid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "SubSystem{" +
                    "fid='" + fid + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
