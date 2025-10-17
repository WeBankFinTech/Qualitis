package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.ImsMetricSchedulerDao;
import com.webank.wedatasphere.qualitis.dao.repository.ImsMetricSchedulerRepository;
import com.webank.wedatasphere.qualitis.entity.ImsMetricScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author v_minminghe
 */
@Repository
public class ImsMetricSchedulerDaoImpl implements ImsMetricSchedulerDao {

    @Autowired
    private ImsMetricSchedulerRepository imsMetricSchedulerRepository;

    @Override
    public List<ImsMetricScheduler> findByDatasource(String dbName, String tableName) {
        return imsMetricSchedulerRepository.findByDatasource(dbName, tableName);
    }

    @Override
    public Optional<ImsMetricScheduler> findById(Long id) {
        return imsMetricSchedulerRepository.findById(id);
    }

    @Override
    public ImsMetricScheduler findByPartition(String dbName, String tableName, String partition) {
        return imsMetricSchedulerRepository.findByPartition(dbName, tableName, partition);
    }

    @Override
    public void saveAll(List<ImsMetricScheduler> imsMetricSchedulerList) {
        imsMetricSchedulerRepository.saveAll(imsMetricSchedulerList);
    }

    @Override
    public void save(ImsMetricScheduler imsMetricScheduler) {
        imsMetricSchedulerRepository.save(imsMetricScheduler);
    }

    @Override
    public void delete(Long id) {
        imsMetricSchedulerRepository.deleteById(id);
    }
}
