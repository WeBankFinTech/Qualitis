package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.LinkisDataSourceEnvDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.LinkisDataSourceEnvRepository;
import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSourceEnv;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2023-12-01 16:42
 * @description
 */
@Repository
public class LinkisDataSourceEnvDaoImpl implements LinkisDataSourceEnvDao {

    @Autowired
    private LinkisDataSourceEnvRepository linkisDataSourceEnvRepository;

    @Override
    public void deleteByEnvIds(List<Long> envIds) {
        linkisDataSourceEnvRepository.deleteByEnvIdIn(envIds);
    }

    @Override
    public void saveAll(List<LinkisDataSourceEnv> linkisDataSourceEnvList) {
        linkisDataSourceEnvRepository.saveAll(linkisDataSourceEnvList);
    }

    @Override
    public List<LinkisDataSourceEnv> queryByLinkisDataSourceId(Long linkisDataSourceId) {
        return linkisDataSourceEnvRepository.findByLinkisDataSourceId(linkisDataSourceId);
    }

    @Override
    public List<LinkisDataSourceEnv> query(Long linkisDataSourceId, List<Long> envIds, List<String> dcnNums, List<String> logicAreas) {
        Specification<LinkisDataSourceEnv> specification = getAdvanceSpecification(linkisDataSourceId, envIds, dcnNums, logicAreas);
        return linkisDataSourceEnvRepository.findAll(specification);
    }

    private Specification<LinkisDataSourceEnv> getAdvanceSpecification(Long linkisDataSourceId, List<Long> envIds, List<String> dcnNums, List<String> logicAreas) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (linkisDataSourceId != null) {
                predicates.add(criteriaBuilder.equal(root.get("linkisDataSourceId"), linkisDataSourceId));
            }
            if (CollectionUtils.isNotEmpty(dcnNums)) {
                predicates.add(root.get("dcnNum").in(dcnNums));
            }
            if (CollectionUtils.isNotEmpty(logicAreas)) {
                predicates.add(root.get("logicArea").in(logicAreas));
            }
            if (CollectionUtils.isNotEmpty(envIds)) {
                predicates.add(root.get("envId").in(envIds));
            }

            Predicate[] p = new Predicate[predicates.size()];
            query.where(criteriaBuilder.and(predicates.toArray(p)));

            return query.getRestriction();
        };
    }
}
