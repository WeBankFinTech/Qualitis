package com.webank.wedatasphere.qualitis.dao;

import com.webank.wedatasphere.qualitis.entity.ApplicationComment;

import java.util.List;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface ApplicationCommentDao {

    /**
     * get By Code
     * @param code
     * @return
     */
    ApplicationComment getByCode(Integer code);

    /**
     * Save all ApplicationComment .
     * @param applicationComments
     * @return
     */
    Set<ApplicationComment> saveAll(List<ApplicationComment> applicationComments);

    /**
     * find All Application Comment
     * @return
     */
    List<ApplicationComment> findAllApplicationComment();
}
