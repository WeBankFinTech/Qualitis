package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.constant.TableDataTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.LinkisDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.LinkisDataSourceRepository;
import com.webank.wedatasphere.qualitis.rule.entity.DataVisibility;
import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author v_minminghe@webank.com
 * @date 2022-09-16 9:58
 * @description
 */
@Repository
public class LinkisDataSourceDaoImpl implements LinkisDataSourceDao {

    @Autowired
    private LinkisDataSourceRepository linkisDataSourceRepository;

    @Override
    public void save(LinkisDataSource linkisDataSource) {
        linkisDataSourceRepository.save(linkisDataSource);
    }

    @Override
    public LinkisDataSource getByLinkisDataSourceId(Long linkisDataSourceId) {
        return linkisDataSourceRepository.findByLinkisDataSourceId(linkisDataSourceId);
    }

    @Override
    public LinkisDataSource getByLinkisDataSourceName(String dataSourceName) {
        return linkisDataSourceRepository.findByLinkisDataSourceName(dataSourceName);
    }

    @Override
    public List<LinkisDataSource> getByLinkisDataSourceNameList(List<String> dataSourceNameList) {
        return linkisDataSourceRepository.findByLinkisDataSourceNameIn(dataSourceNameList);
    }

    @Override
    public List<LinkisDataSource> getByLinkisDataSourceIds(List<Long> dataSourceIds) {
        return linkisDataSourceRepository.findByLinkisDataSourceIdIn(dataSourceIds);
    }

    @Override
    public Page<LinkisDataSource> filterWithPage(
            String dataSourceName, Long dataSourceTypeId, List<Long> dataVisibilityDeptList
            , String createUser, String searchCreateUser, String searchModifyUser
            , String subSystemName, Long devDepartmentId, Long opsDepartmentId
            , boolean ignoreDataAuthorityCondition, List<Long> searchDataVisibilityDeptList, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return linkisDataSourceRepository.findAll((root, query, builder) -> {
            List<Predicate> andPredicates = new ArrayList<>();

            if (StringUtils.isNotBlank(dataSourceName)) {
                andPredicates.add(builder.like(root.get("linkisDataSourceName"), "%" + dataSourceName + "%"));
            }
            if (Objects.nonNull(dataSourceTypeId)) {
                andPredicates.add(builder.equal(root.get("dataSourceTypeId"), dataSourceTypeId));
            }
            if (StringUtils.isNotBlank(searchCreateUser)) {
                andPredicates.add(builder.equal(root.get("createUser"), searchCreateUser));
            }
            if (StringUtils.isNotBlank(searchModifyUser)) {
                andPredicates.add(builder.equal(root.get("modifyUser"), searchModifyUser));
            }
            if (StringUtils.isNotBlank(subSystemName)) {
                andPredicates.add(builder.equal(root.get("subSystem"), subSystemName));
            }
            if (Objects.nonNull(devDepartmentId)) {
                andPredicates.add(builder.equal(root.get("devDepartmentId"), devDepartmentId));
            }
            if (Objects.nonNull(opsDepartmentId)) {
                andPredicates.add(builder.equal(root.get("opsDepartmentId"), opsDepartmentId));
            }
            if (CollectionUtils.isNotEmpty(searchDataVisibilityDeptList)) {
                Subquery<Long> subQuery = query.subquery(Long.class);
                Root<DataVisibility> subRoot = subQuery.from(DataVisibility.class);
                subQuery.select(subRoot.get("tableDataId"));
                subQuery.where(builder.and(
                        builder.equal(subRoot.get("tableDataType"), TableDataTypeEnum.LINKIS_DATA_SOURCE.getCode()),
                        subRoot.get("departmentSubId").in(searchDataVisibilityDeptList),
                        builder.equal(subRoot.get("tableDataId"), root.get("id"))
                ));
                andPredicates.add(builder.exists(subQuery));
            }

//            忽视数据权限的查询条件限制，一般用于管理员
            if (!ignoreDataAuthorityCondition && CollectionUtils.isNotEmpty(dataVisibilityDeptList)) {
                List<Predicate> orPredicates = new ArrayList<>();
                if (StringUtils.isNotBlank(createUser)) {
                    orPredicates.add(builder.equal(root.get("createUser"), createUser));
                }
                orPredicates.add(root.get("devDepartmentId").in(dataVisibilityDeptList));
                orPredicates.add(root.get("opsDepartmentId").in(dataVisibilityDeptList));

                Subquery<Long> subQuery = query.subquery(Long.class);
                Root<DataVisibility> subRoot = subQuery.from(DataVisibility.class);
                subQuery.select(subRoot.get("tableDataId"));
                subQuery.where(builder.and(
                        builder.equal(subRoot.get("tableDataType"), TableDataTypeEnum.LINKIS_DATA_SOURCE.getCode()),
                        subRoot.get("departmentSubId").in(dataVisibilityDeptList),
                        builder.equal(subRoot.get("tableDataId"), root.get("id"))
                ));

//                添加子查询
                orPredicates.add(builder.exists(subQuery));
//                将()内的or语句添加到and条件中去
                andPredicates.add(builder.or(orPredicates.toArray(new Predicate[orPredicates.size()])));
            }
            query.where(builder.and(andPredicates.toArray(new Predicate[andPredicates.size()])));
            return query.getRestriction();
        }, pageable);
    }

    @Override
    public List<String> getAllDataSourceNameList() {
        return linkisDataSourceRepository.findAllLinkisDataSourceNameList();
    }

    @Override
    public List<LinkisDataSource> getAllDataSourceEnvsIsNotNull() {
        return linkisDataSourceRepository.findAll(((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(root.get("envs").isNotNull());
            predicates.add(criteriaBuilder.notEqual(root.get("envs"), ""));
            Predicate[] p = new Predicate[predicates.size()];
            query.where(criteriaBuilder.and(predicates.toArray(p)));

            return query.getRestriction();
        }));
    }

}
