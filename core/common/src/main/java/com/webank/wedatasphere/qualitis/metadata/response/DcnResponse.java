package com.webank.wedatasphere.qualitis.metadata.response;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.qualitis.util.map.CustomObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author allenzhou@webank.com
 * @date 2023/4/25 14:45
 */
public class DcnResponse {

    private final Logger LOGGER = LoggerFactory.getLogger(DcnResponse.class);

    Map<Object, Map<Object, List<Map<String, Object>>>> res = Maps.newHashMap();

    public DcnResponse() {
        // default
    }

    public DcnResponse(List<Map<String, Object>> maps) {
        this.res = maps.stream().filter(map -> {
                    boolean legalDcn = Objects.nonNull(map.get("idc")) && Objects.nonNull(map.get("logic_dcn"));
                    if (!legalDcn) {
                        LOGGER.warn("idc or logic dcn is null, data: {}", CustomObjectMapper.transObjectToJson(map));
                    }
                    return legalDcn;
                })
                .collect(Collectors.groupingBy(
                        map -> map.get("idc"),
                        Collectors.groupingBy(map -> map.get("logic_dcn")
                        )));
    }

    public Map<Object, Map<Object, List<Map<String, Object>>>> getRes() {
        return res;
    }

    public void setRes(Map<Object, Map<Object, List<Map<String, Object>>>> res) {
        this.res = res;
    }
}
