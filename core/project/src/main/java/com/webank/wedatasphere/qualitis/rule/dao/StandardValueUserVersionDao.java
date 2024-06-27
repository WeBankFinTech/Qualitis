package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.StandardValueUserVersion;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface StandardValueUserVersionDao {

    /**
     * save StandardValueUserVersion
     * @param standardValueUserVersion
     * @return
     */
    StandardValueUserVersion saveStandardValueUserVersion(StandardValueUserVersion standardValueUserVersion);

    /**
     * find  StandardValueUserVersion list
     * @param standardVauleVersionId
     * @return
     */
    List<StandardValueUserVersion> findListStandardValueUserVersion(Long standardVauleVersionId);

    /**
     * delete StandardValueUserVersion
     * @param standardValueUserVersion
     */
    void deleteStandardValueUserVersion(StandardValueUserVersion standardValueUserVersion);


}
