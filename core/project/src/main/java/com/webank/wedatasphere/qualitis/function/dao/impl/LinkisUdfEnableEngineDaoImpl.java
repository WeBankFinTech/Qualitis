package com.webank.wedatasphere.qualitis.function.dao.impl;

import com.webank.wedatasphere.qualitis.function.dao.LinkisUdfEnableEngineDao;
import com.webank.wedatasphere.qualitis.function.dao.repository.LinkisUdfEnableEngineRepository;
import com.webank.wedatasphere.qualitis.function.entity.LinkisUdfEnableEngine;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author allenzhou@webank.com
 * @date 2022/11/7 17:35
 */
@Repository
public class LinkisUdfEnableEngineDaoImpl implements LinkisUdfEnableEngineDao {

    @Autowired
    private LinkisUdfEnableEngineRepository linkisUdfEnableEngineRepository;

    @Override
    public void saveAll(List<LinkisUdfEnableEngine> linkisUdfEnableEngines) {
        linkisUdfEnableEngineRepository.saveAll(linkisUdfEnableEngines);
    }

    @Override
    public void deleteInBatch(Set<LinkisUdfEnableEngine> linkisUdfEnableEngines) {
        linkisUdfEnableEngineRepository.deleteInBatch(linkisUdfEnableEngines);
    }
}
