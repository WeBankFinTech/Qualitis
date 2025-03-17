package com.webank.wedatasphere.qualitis.function.dao.impl;

import com.webank.wedatasphere.qualitis.function.dao.LinkisUdfEnableClusterDao;
import com.webank.wedatasphere.qualitis.function.dao.repository.LinkisUdfEnableClusterRepository;
import com.webank.wedatasphere.qualitis.function.entity.LinkisUdfEnableCluster;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author allenzhou@webank.com
 * @date 2022/11/7 17:36
 */
@Repository
public class LinkisUdfEnableClusterDaoImpl implements LinkisUdfEnableClusterDao {

    @Autowired
    private LinkisUdfEnableClusterRepository linkisUdfEnableClusterRepository;

    @Override
    public void saveAll(List<LinkisUdfEnableCluster> linkisUdfEnableClusters) {
        linkisUdfEnableClusterRepository.saveAll(linkisUdfEnableClusters);
    }

    @Override
    public void deleteInBatch(Set<LinkisUdfEnableCluster> abandonlinkisUdfEnableClusterSet) {
        linkisUdfEnableClusterRepository.deleteInBatch(abandonlinkisUdfEnableClusterSet);
    }
}
