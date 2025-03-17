package com.webank.wedatasphere.qualitis.function.dao;

import com.webank.wedatasphere.qualitis.function.entity.LinkisUdfEnableEngine;
import java.util.List;
import java.util.Set;

/**
 * @author allenzhou@webank.com
 * @date 2022/11/7 17:30
 */
public interface LinkisUdfEnableEngineDao {

    /**
     * Save all
     * @param linkisUdfEnableEngines
     */
    void saveAll(List<LinkisUdfEnableEngine> linkisUdfEnableEngines);

    /**
     * Delete by linkis udf
     * @param linkisUdfEnableEngines
     */
    void deleteInBatch(Set<LinkisUdfEnableEngine> linkisUdfEnableEngines);
}
