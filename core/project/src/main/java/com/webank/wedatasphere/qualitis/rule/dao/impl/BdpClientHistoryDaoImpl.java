package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.BdpClientHistoryDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.BdpClientHistoryRepository;
import com.webank.wedatasphere.qualitis.rule.entity.BdpClientHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author allenzhou@webank.com
 * @date 2021/8/17 14:50
 */
@Repository
public class BdpClientHistoryDaoImpl implements BdpClientHistoryDao {
    @Autowired
    private BdpClientHistoryRepository bdpClientHistoryRepository;

    @Override
    public BdpClientHistory findByRuleId(Long ruleId) {
        return bdpClientHistoryRepository.findByRuleId(ruleId);
    }

    @Override
    public BdpClientHistory save(BdpClientHistory bdpClientHistory) {
        return bdpClientHistoryRepository.save(bdpClientHistory);
    }

    @Override
    public void delete(BdpClientHistory bdpClientHistory) {
        bdpClientHistoryRepository.delete(bdpClientHistory);
    }

    @Override
    public BdpClientHistory findByTemplateFunctionAndDatasourceAndProjectName(String templateFuncName, String datasource, String projectName) {
        return bdpClientHistoryRepository.findByTemplateFunctionAndDatasourceAndProjectName(templateFuncName, datasource, projectName);
    }
}
