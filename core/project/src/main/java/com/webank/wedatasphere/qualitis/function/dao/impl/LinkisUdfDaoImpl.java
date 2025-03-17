package com.webank.wedatasphere.qualitis.function.dao.impl;

import com.webank.wedatasphere.qualitis.function.dao.LinkisUdfDao;
import com.webank.wedatasphere.qualitis.function.dao.repository.LinkisUdfRepository;
import com.webank.wedatasphere.qualitis.function.entity.LinkisUdf;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

/**
 * @author allenzhou@webank.com
 * @date 2022/11/7 17:30
 */
@Repository
public class LinkisUdfDaoImpl implements LinkisUdfDao {

    @Autowired
    private LinkisUdfRepository linkisUdfRepository;

    @Override
    public LinkisUdf save(LinkisUdf linkisUdf) {
        return linkisUdfRepository.save(linkisUdf);
    }

    @Override
    public LinkisUdf findById(Long id) {
        return linkisUdfRepository.findById(id).orElse(null);
    }

    @Override
    public LinkisUdf findByName(String name) {
        return linkisUdfRepository.findByName(name);
    }

    @Override
    public Page<LinkisUdf> filter(String name, String cnName, String dir, Integer implType, List<Integer> enableEngine, List<String> enableCluster
        , String createUser, String modifyUser, String tableDataType, List<Long> dataVisibilityDeptList, String loginUser, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);

        return linkisUdfRepository.filter(name, cnName, dir, implType, enableEngine, enableCluster, createUser, modifyUser, tableDataType, dataVisibilityDeptList, loginUser, pageable);
    }

    @Override
    public Page<LinkisUdf> filterAll(String name, String cnName, String dir, Integer implType, List<Integer> enableEngine, List<String> enableCluster
        , String createUser, String modifyUser, Long devDepartmentId, Long opsDepartmentId, List<Long> dataVisibilityIds, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);

        return linkisUdfRepository.filterAll(name, cnName, dir, implType, enableEngine, enableCluster, createUser, modifyUser, devDepartmentId, opsDepartmentId, dataVisibilityIds, pageable);
    }

    @Override
    public int countAll(String name, String cnName, String dir, Integer implType, List<Integer> enableEngine, List<String> enableCluster
        , String createUser, String modifyUser, Long devDepartmentId, Long opsDepartmentId) {
        return linkisUdfRepository.countAll(name, cnName, dir, implType, enableEngine, enableCluster, createUser, modifyUser, devDepartmentId, opsDepartmentId);
    }

    @Override
    public void delete(LinkisUdf linkisUdf) {
        linkisUdfRepository.delete(linkisUdf);
    }
}
