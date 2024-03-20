package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.ApplicationCommentDao;
import com.webank.wedatasphere.qualitis.dao.repository.ApplicationCommentRepository;
import com.webank.wedatasphere.qualitis.entity.ApplicationComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
@Repository
public class ApplicationCommentDaoImpl implements ApplicationCommentDao {

    @Autowired
    private ApplicationCommentRepository repository;

    @Override
    public ApplicationComment getByCode(Integer code) {
        return repository.getByCode(code);
    }

    @Override
    public Set<ApplicationComment> saveAll(List<ApplicationComment> applicationComments) {
        Set<ApplicationComment> result = new HashSet<>();
        result.addAll(repository.saveAll(applicationComments));
        return result;
    }

    @Override
    public List<ApplicationComment> findAllApplicationComment() {
        return repository.findAll();
    }
}
