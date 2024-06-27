package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.ServiceInfoDao;
import com.webank.wedatasphere.qualitis.dao.repository.ServiceInfoRepository;
import com.webank.wedatasphere.qualitis.entity.ServiceInfo;
import com.webank.wedatasphere.qualitis.entity.TenantUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2022/2/23 15:40
 */
@Repository
public class ServiceInfoDaoImpl implements ServiceInfoDao {
    @Autowired
    private ServiceInfoRepository serviceInfoRepository;

    @Override
    public ServiceInfo save(ServiceInfo serviceInfo) {
        return serviceInfoRepository.save(serviceInfo);
    }

    @Override
    public void delete(Long serviceInfoId) {
        serviceInfoRepository.deleteById(serviceInfoId);
    }

    @Override
    public ServiceInfo findById(Long serviceId) {
        return serviceInfoRepository.findById(serviceId).get();
    }

    @Override
    public List<ServiceInfo> pageServiceInfo(String ip, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return serviceInfoRepository.pageServiceInfo("%" + ip + "%", pageable).getContent();
    }

    @Override
    public long countAll(String ip) {
        return serviceInfoRepository.countByIp("%" + ip + "%");
    }

    @Override
    public List<ServiceInfo> findByStatus(Integer code) {
        return serviceInfoRepository.findByStatus(code);
    }

    @Override
    public List<ServiceInfo> findByTenantUser(TenantUser tenantUser) {
        return serviceInfoRepository.findByTenantUser(tenantUser);
    }

    @Override
    public List<ServiceInfo> findNonRelatedTenantUser() {
        return serviceInfoRepository.findNullTenantUser();
    }
}
