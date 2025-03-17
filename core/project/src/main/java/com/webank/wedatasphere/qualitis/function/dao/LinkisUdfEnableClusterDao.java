package com.webank.wedatasphere.qualitis.function.dao;

import com.webank.wedatasphere.qualitis.function.entity.LinkisUdfEnableCluster;
import java.util.List;
import java.util.Set;

/**
 * @author allenzhou@webank.com
 * @date 2022/11/7 17:30
 */
public interface LinkisUdfEnableClusterDao {

    /**
     * Save all
     * @param linkisUdfEnableClusters
     */
    void saveAll(List<LinkisUdfEnableCluster> linkisUdfEnableClusters);

    /**
     * Delete all
     * @param abandonlinkisUdfEnableClusterSet
     */
    void deleteInBatch(Set<LinkisUdfEnableCluster> abandonlinkisUdfEnableClusterSet);
}
